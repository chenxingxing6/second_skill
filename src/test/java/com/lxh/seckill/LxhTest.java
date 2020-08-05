package com.lxh.seckill;

/**
 * created by lanxinghua@2dfire.com on 2020/8/5
 */
public class LxhTest {
    public static void main(String[] args) throws Exception{
        // 定义1M字节缓冲区
//        HashMap map = new HashMap();
//        for (int i = 0; i < 1000; i++) {
//            TimeUnit.SECONDS.sleep(1);
//            Byte[] b = new Byte[1024*1024];
//            map.put(i, b);
//            long l = Runtime.getRuntime().freeMemory() / 1024 /1024;
//            System.out.println(l);
//        }
        byte[] bytes = "https://blog.csdn.net/wgw335363240/article/details/8878644".getBytes();
        System.out.println(bytes.length);
        System.out.println((bytes.length * 10000000000L) / 1024 /1024 / 1024 + "MB");
    }
}
