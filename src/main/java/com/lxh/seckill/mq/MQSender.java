package com.lxh.seckill.mq;

import com.lxh.seckill.mq.common.MQConfiguation;
import com.lxh.seckill.mq.common.MQProperties;
import com.lxh.seckill.redis.RedisService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MQSender {
	private static Logger log = LoggerFactory.getLogger(MQSender.class);
	@Autowired
	private DefaultMQProducer defaultMQProducer;
	@Autowired
	private MQConfiguation mqConfiguation;

	public void sendSeckillMessage(SeckillMessage msg) {
		try {
			String msgStr = RedisService.beanToString(msg);
			log.info("send message:"+msgStr);
			MQProperties mqProperties = mqConfiguation.getMqProperties();
			Message sendMsg = new Message(mqProperties.getTopic(), "test_tag", msgStr.getBytes(RemotingHelper.DEFAULT_CHARSET));
			defaultMQProducer.send(sendMsg);
		}catch (Exception e){
			log.error("消息发送失败", e);
		}
	}

}
