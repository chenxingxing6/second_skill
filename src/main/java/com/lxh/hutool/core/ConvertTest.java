package com.lxh.hutool.core;

import cn.hutool.core.convert.Convert;

/**
 * created by lanxinghua@2dfire.com on 2020/8/5
 */
public class ConvertTest {
    public static void main(String[] args) {
        String s = Convert.toStr(1);
        String aDefault = Convert.toStr(null, "default");
        System.out.println(aDefault);

        // 半角全角转换
        String abc = Convert.toSBC("abc");
        System.out.println(abc);

        // 金额大小写转换
        String s1 = Convert.digitToChinese(123.5d);
        System.out.println(s1);

        //
    }
}
