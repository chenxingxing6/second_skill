package com.lxh.seckill.other.lock;

import com.lxh.seckill.common.LockTypeEnum;
import com.lxh.seckill.dao.MyLockMapper;
import com.lxh.seckill.entity.MyLock;
import com.lxh.seckill.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 * Redis实现分布式锁
 */
@Component
public class Lock4 {
    @Autowired
    private RedisService redisService;

    public boolean getLock(String key, String value, int expireSecond){
        try {
            Long isLock = redisService.setnx(key, expireSecond, value);
            if (isLock == 1){
               return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void unLock(String key, String value){
        String val = redisService.get(key, String.class);
        if (StringUtils.isNotEmpty(val) && val.equals(value)){
            redisService.del(key);
        }
    }
}
