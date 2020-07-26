package com.lxh.seckill.other.lock;

import com.lxh.seckill.common.LockTypeEnum;
import com.lxh.seckill.dao.MyLockMapper;
import com.lxh.seckill.entity.MyLock;
import com.lxh.seckill.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 * Redis实现分布式锁
 */
@Component
public class Lock4 {
    @Autowired
    private RedisService redisService;
    private static String STOCK_LUA;

    static {
        /**
         * @desc 扣减库存Lua脚本
         * 库存（stock）-1：表示不限库存
         * 库存（stock）0：表示没有库存
         * 库存（stock）大于0：表示剩余库存
         * @params 库存key
         * @return
         * 		-3:库存未初始化
         * 		-2:库存不足
         * 		-1:不限库存
         * 		大于等于0:剩余库存（扣减之后剩余的库存）
         * 	    redis缓存的库存(value)是-1表示不限库存，直接返回1
         */
        StringBuilder sb = new StringBuilder();
        sb.append("if (redis.call('exists', KEYS[1]) == 1) then");
        sb.append("    local stock = tonumber(redis.call('get', KEYS[1]));");
        sb.append("    local num = tonumber(ARGV[1]);");
        sb.append("    if (stock == -1) then");
        sb.append("        return -1;");
        sb.append("    end;");
        sb.append("    if (stock >= num) then");
        sb.append("        return redis.call('incrby', KEYS[1], 0 - num);");
        sb.append("    end;");
        sb.append("    return -2;");
        sb.append("end;");
        sb.append("return -3;");
        STOCK_LUA = sb.toString();
    }

    /**
     * lua脚本减库存
     * @param keys
     * @param args
     * @return
     */
   public Integer eval(List<String> keys, List<String> args){
       Object eval = redisService.eval(STOCK_LUA, keys, args);
       System.out.println(eval);
       return null;
   }

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
