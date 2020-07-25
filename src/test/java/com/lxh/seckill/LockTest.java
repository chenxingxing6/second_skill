package com.lxh.seckill;

import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.dao.GoodsMapper;
import com.lxh.seckill.entity.Goods;
import com.lxh.seckill.entity.OrderInfo;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.other.lock.Lock1;
import com.lxh.seckill.redis.GoodsKey;
import com.lxh.seckill.redis.RedisService;
import com.lxh.seckill.service.SeckillGoodsService;
import com.lxh.seckill.service.SeckillOrderService;
import com.lxh.seckill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.image.Kernel;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * created by lanxinghua@2dfire.com on 2020/7/24
 * 分布式锁
 * 一、Mysql
 * 1.1 悲观锁
 * @Query(value = "select t from LockEntity t where t.lockType =?1 ")
 * LockEntity getLock( String lockType);
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LockTest {
    @Autowired
    UserService userService;
    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    RedisService redisService;
    @Autowired
    Lock1 lock1;
    private AtomicInteger num = new AtomicInteger(0);


    @Test
    public void test01() throws Exception{
        List<User> users = userService.selectAll();
        users = users.stream().limit(500).collect(Collectors.toList());
        Long goodId = 1L;
        CyclicBarrier barrier = new CyclicBarrier(users.size());
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (User user : users) {
            executorService.execute(()->{
                try {
                    barrier.await();
                    handler_redis(user, goodId);
                    //handler_cas(user, goodId);
                    //handler_x_lock(user, goodId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }


    /**
     * 唯一索引
     */
    private void handler_db_unique(User user, Long goodId){
        GoodsBo goodsBo = seckillGoodsService.getseckillGoodsBoByGoodsId(goodId);
        if (Objects.isNull(goodsBo) || Objects.isNull(user)){
            return;
        }
        OrderInfo insert = seckillOrderService.insert(user, goodsBo);
        if (insert==null){
            System.out.println("秒杀失败");
        }
    }


    /**
     * X排他锁实现+事务
     * @param user
     */
    private void handler_x_lock(User user, Long goodId){
        GoodsBo goodsBo = seckillGoodsService.getseckillGoodsBoByGoodsId(goodId);
        if (Objects.isNull(goodsBo) || Objects.isNull(user)){
            return;
        }
        OrderInfo insert = seckillOrderService.insert(user, goodsBo);
        if (insert==null){
            System.out.println("秒杀失败");
        }
    }


    /**
     * 乐观锁实现
     * @param user
     */
    private void handler_cas(User user, Long goodId){
        Goods goods = goodsMapper.selectByPrimaryKey(goodId);
        // 需要加一个version
        // 更新时判断version是否相等
        goodsMapper.updateByPrimaryKey(goods);
    }


    /**
     * 单线程redis预减库存
     * @param user
     */
    private void handler_redis(User user, Long goodId){
        String key = "MY_KEY_"+goodId;
        try {
            Long isLock = redisService.setnx(key, 10, String.valueOf(user.getId()));
            if (isLock == 1){
                System.out.println("do business....");
                TimeUnit.SECONDS.sleep(10);
            }
            System.out.println("没获取到锁.....");
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            String s = redisService.get(key, String.class);
            if (s.equals(user.getId())){
                redisService.del(key);
            }
        }
    }
}