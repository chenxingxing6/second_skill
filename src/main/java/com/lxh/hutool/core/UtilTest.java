package com.lxh.hutool.core;

import cn.hutool.core.lang.Console;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.*;
import com.lxh.seckill.entity.User;

import java.lang.reflect.Method;

/**
 * created by lanxinghua@2dfire.com on 2020/8/6
 */
public class UtilTest {
    public static void main(String[] args) {
        // hash工具类
        int lanxinghua = HashUtil.oneByOneHash("lanxinghua");
        System.out.println(lanxinghua);

        // 反射工具类
        Method setUserName = ReflectUtil.getMethod(User.class, "getId");
        System.out.println(setUserName.getName());

        // 命令行工具
        String ipconfig = RuntimeUtil.execForStr("ipconfig");
        //Console.log(ipconfig);

        // 唯一Id工具
        System.out.println("uuid："+IdUtil.randomUUID());
        System.out.println("objectId："+IdUtil.objectId());
        System.out.println("snowflakeId："+IdUtil.getSnowflake(1,1).nextIdStr());

        // 正则表达
        boolean result = ReUtil.isMatch("\\w+[\u4E00-\u9FFF]+\\d+", "ZZZaaabbbccc中文1234");
        System.out.println(result);
    }
}
