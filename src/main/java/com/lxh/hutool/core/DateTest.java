package com.lxh.hutool.core;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Month;
import cn.hutool.core.date.TimeInterval;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * created by lanxinghua@2dfire.com on 2020/8/5
 */
public class DateTest {
    public static void main(String[] args) throws Exception{
        // 2020-08-05 22:33:22
        System.out.println(DateUtil.now());
        System.out.println(DateUtil.today());

        DateTime parse = DateUtil.parse("2020-08-05");
        System.out.println(parse.toString());

        String format = DateUtil.format(new Date(), "yyyy-MM-dd");
        System.out.println(format);

        // 时间格式化
        Date date = new Date();
        int year = DateUtil.year(date);
        Month month = DateUtil.monthEnum(date);
        System.out.println(year + "-" + month.getValue());

        // 开始时间，结束时间
        System.out.println(DateUtil.beginOfDay(date).toString());
        System.out.println(DateUtil.endOfDay(date).toString());

        // 是否为闰年
        System.out.println(DateUtil.isLeapYear(2020));

        // 计时器
        TimeInterval timer = DateUtil.timer();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("秒："+timer.intervalSecond());
    }
}
