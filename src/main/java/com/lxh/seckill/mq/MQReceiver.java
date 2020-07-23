package com.lxh.seckill.mq;

import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.common.Const;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.mq.common.AbstractRocketConsumer;
import com.lxh.seckill.redis.RedisService;
import com.lxh.seckill.redis.SeckillKey;
import com.lxh.seckill.service.OrderService;
import com.lxh.seckill.service.SeckillGoodsService;
import com.lxh.seckill.service.SeckillOrderService;
import com.lxh.seckill.service.UserService;
import com.lxh.seckill.thread.ThreadPoolScheduleManager;
import com.lxh.seckill.util.MD5Util;
import com.lxh.seckill.websocket.WebSocketServer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MQReceiver extends AbstractRocketConsumer {
	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
	@Autowired
	RedisService redisService;
	@Autowired
	SeckillGoodsService goodsService;
	@Autowired
    OrderService orderService;
	@Autowired
    SeckillOrderService seckillOrderService;
	@Autowired
	WebSocketServer webSocketServer;
	@Autowired
    UserService userService;
	private AtomicInteger num = new AtomicInteger(0);
	private volatile int stock = 0;

	@Override
	public void init() {
		// 设置主题,标签与消费者标题
		super.config("skill", "*", "标题");
		registerMessageListener(new MessageListenerConcurrently() {
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
				list.forEach(msg->{
					String content = new String(msg.getBody());
					String keyMd5 = MD5Util.md5(msg.getTags() + msg.getMsgId() + msg.getKeys());
					receive(content, keyMd5);
				});
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
	}

	public void receive(String content, String md5) {
		if (redisService.setnx(md5, 5, "1") == 0){
			System.out.println("消息重复消费");
			return;
		}
		SeckillMessage mm  = RedisService.stringToBean(content, SeckillMessage.class);
		User user = mm.getUser();
		long goodsId = mm.getGoodsId();
		Long grabTime = mm.getTime();

		//判断是否已经秒杀到了
		String key = user.getId() + ":" + goodsId;
		Boolean aBoolean = redisService.get(SeckillKey.skillUser, key, Boolean.class);
		if (aBoolean != null && Boolean.TRUE == aBoolean){
			return;
		}


		// 用户排名sorted Set
		redisService.zadd("activity_"+goodsId, grabTime, String.valueOf(user.getId()), 10*60);

		// 分布式锁(5s内抢完，延时发送消息)
		if(redisService.setnx("activity_lock"+goodsId, 10, "1") == 1){
			ScheduledExecutorService scheduledExecutorService = ThreadPoolScheduleManager.getInstance();
			scheduledExecutorService.schedule(() -> {
				execute5SecondsDeliver(goodsId);
			}, 3, TimeUnit.SECONDS);

		}
	}

	private void execute5SecondsDeliver(Long goodId){
		// 根据抢的时间进行升序排序
		List<String> userIds = redisService.zrange("activity_"+goodId,0, -1, 1);
		if (CollectionUtils.isEmpty(userIds)){
			return;
		}
		List<User> users = userService.selectAll();
		GoodsBo goodsBo = goodsService.getseckillGoodsBoByGoodsId(goodId);
		Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(e -> e.getId(), u -> u));
		//减库存,下订单,写入秒杀订单
		userIds = userIds.stream().limit(100).collect(Collectors.toList());
		for (String userId : userIds) {
			User user = userMap.get(Integer.valueOf(userId));
			if (Objects.isNull(user)){
				continue;
			}
			webSocketServer.sendMsg(String.format("【排名：%s】，用户名：%s，秒杀商品：%s", num.incrementAndGet(), user.getUserName(), goodsBo.getGoodsName()));
			String key = user.getId() + ":" + goodId;
			redisService.set(SeckillKey.skillUser, key, Boolean.TRUE, Const.RedisCacheExtime.Second60);
			seckillOrderService.insert(user, goodsBo);
		}
	}
}
