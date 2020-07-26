package com.lxh.seckill.dao;

import com.lxh.seckill.entity.MyLock;

public interface MyLockMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByLockType(String lockType);

    int insert(MyLock record);

    int insertSelective(MyLock record);

    MyLock selectByPrimaryKey(Integer id);

    MyLock selectByXLock(String lockType);

    MyLock selectByLockType(String lockType);

    int updateByPrimaryKeySelective(MyLock record);

    int updateByPrimaryKey(MyLock record);
}
