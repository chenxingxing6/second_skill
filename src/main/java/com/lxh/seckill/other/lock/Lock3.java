package com.lxh.seckill.other.lock;

import com.lxh.seckill.common.LockTypeEnum;
import com.lxh.seckill.dao.MyLockMapper;
import com.lxh.seckill.entity.MyLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 * 基于乐观锁实现分布式锁
 */
@Component
public class Lock3 {
    @Autowired
    private MyLockMapper myLockMapper;

    public MyLock getLock(LockTypeEnum typeEnum){
        return myLockMapper.selectByLockType(typeEnum.getCode());
    }

    public void updateLock(MyLock lock){
        int i = myLockMapper.updateByPrimaryKeySelective(lock);
        System.out.println(i>0 ? "更新成功..." : "更新失败...");
    }
}
