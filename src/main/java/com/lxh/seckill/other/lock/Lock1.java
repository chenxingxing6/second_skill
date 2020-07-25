package com.lxh.seckill.other.lock;

import com.lxh.seckill.common.LockTypeEnum;
import org.springframework.stereotype.Component;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 * 基于唯一索引失效分布式锁
 */
@Component
public class Lock1 {
    public Boolean getLock(LockTypeEnum typeEnum){
        return null;
    }


    public Boolean unLock(LockTypeEnum typeEnum){
        return null;
    }
}
