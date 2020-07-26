package com.lxh.seckill;

import com.google.common.collect.Lists;
import com.lxh.seckill.common.LockTypeEnum;
import com.lxh.seckill.dao.GoodsMapper;
import com.lxh.seckill.entity.MyLock;
import com.lxh.seckill.entity.User;
import com.lxh.seckill.other.lock.*;
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

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * created by lanxinghua@2dfire.com on 2020/7/24
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
    @Autowired
    Lock2 lock2;
    @Autowired
    Lock3 lock3;
    @Autowired
    Lock4 lock4;
    @Autowired
    Lock5 lock5;

    @Test
    public void test01() throws Exception{
        List<User> users = userService.selectAll();
        users = users.stream().limit(100).collect(Collectors.toList());
        Long goodId = 1L;
        for (User user : users) {
            new Thread(()->{
                //handler_db_unique(user, goodId);
                //handler_x_lock(user, goodId);
                //handler_cas_lock(user, goodId);
                //handler_redis(user, goodId);
                handler_zk(user, goodId);
            }).start();
        }
        Thread.sleep(1000* 10);
    }

    @Test
    public void test02(){
        lock4.eval(Lists.newArrayList("lxh"), Lists.newArrayList("1"));
    }


    /**
     * 唯一索引
     */
    private void handler_db_unique(User user, Long goodId){
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(100));
            Boolean lock = lock1.getLock(LockTypeEnum.REFRESH_TOKEN);
            if (lock){
                System.out.println("获取锁....");
                TimeUnit.SECONDS.sleep(1);
            }else {
                System.out.println("失败..................");
            }
        }catch (Exception e){
        }finally {
            System.out.println("解锁....");
            lock1.unLock(LockTypeEnum.REFRESH_TOKEN);
        }
    }


    /**
     * X排他锁实现
     * @param user
     */
    private void handler_x_lock(User user, Long goodId){
        MyLock lock = lock2.getLock(LockTypeEnum.REFRESH_TOKEN);
        if (lock == null){
            System.out.println("失败....");
            return;
        }else {
            System.out.println("获取到锁...");
            boolean flag = (System.currentTimeMillis() - lock.getGmtModify().getTime()) > 1000*10;
            if (flag){
                lock.setGmtModify(new Date());
                lock2.updateLock(lock);
                System.out.println("更新锁时间...");
            }
        }
    }

    /**
     * 乐观锁实现
     * @param user
     */
    private void handler_cas_lock(User user, Long goodId){
        MyLock lock = lock3.getLock(LockTypeEnum.REFRESH_TOKEN);
        if (lock == null){
            System.out.println("失败....");
            return;
        }else {
            System.out.println("获取到锁...");
            lock.setGmtModify(new Date());
            lock3.updateLock(lock);
        }
    }

    /**
     * redis
     */
    private void handler_redis(User user, Long goodId){
        String key = "MY_KEY_"+goodId;
        try {
            boolean lock = lock4.getLock(key, String.valueOf(user.getId()), 10);
            if (lock){
                System.out.println("获取到锁....");
            }else {
                System.out.println("没获取到锁");
            }
        }finally {
            lock4.unLock(key, String.valueOf(user.getId()));
        }
    }

    /**
     * zk
     */
    private void handler_zk(User user, Long goodId){
        String key = "MY_KEY_"+goodId;
        try {
            Boolean lock = lock5.tryLock(key);
            if (lock){
                System.out.println("获取到锁....");
            }else {
                System.out.println("没获取到锁");
            }
        }finally {
            lock5.unLock(key);
        }
    }
}
