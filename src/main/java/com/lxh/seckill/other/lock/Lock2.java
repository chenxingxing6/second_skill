package com.lxh.seckill.other.lock;

import com.alibaba.fastjson.JSON;
import com.lxh.seckill.common.LockTypeEnum;
import com.lxh.seckill.dao.MyLockMapper;
import com.lxh.seckill.entity.MyLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 * 基于悲观锁实现分布式锁
 */
@Component
public class Lock2 {
    @Autowired
    private MyLockMapper myLockMapper;

    public MyLock getLock(LockTypeEnum typeEnum){
        try {
            MyLock lock = myLockMapper.selectByXLock(typeEnum.getCode());
            if (lock != null){
                return lock;
            }
            lock = new MyLock();
            lock.setGmtModify(new Date());
            lock.setLockType(typeEnum.getCode());
            lock.setLockDesc(typeEnum.getDesc());
            myLockMapper.insert(lock);
            return lock;
        }catch (Exception e){
            return null;
        }
    }

    public void updateLock(MyLock lock){
        int i = myLockMapper.updateByPrimaryKeySelective(lock);
        System.out.println(i>0 ? "更新成功..." : "更新失败...");
    }
}
