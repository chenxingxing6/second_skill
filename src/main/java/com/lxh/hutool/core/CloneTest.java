package com.lxh.hutool.core;

import cn.hutool.core.util.ObjectUtil;
import com.lxh.seckill.entity.User;

/**
 * created by lanxinghua@2dfire.com on 2020/8/5
 */
public class CloneTest {
    public static void main(String[] args) {
        deepClone();
    }

    // 深拷贝
    private static void deepClone(){
        User user = new User();
        user.setUserName("before");
        User user1 = ObjectUtil.cloneByStream(user);
        user1.setUserName("after");
        System.out.println(user.getUserName());
        System.out.println(user1.getUserName());
    }
}
