package com.lxh.hutool.bloomFilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;

/**
 * created by lanxinghua@2dfire.com on 2020/8/6
 * 布隆过滤器
 */
public class BloomFilterTest {
    public static void main(String[] args) {
        int size = 1000;
        BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(size);
        for (int i = 0; i < size; i++) {
            bitMapBloomFilter.add(i + "");
        }
        boolean lxh = bitMapBloomFilter.contains("lxh");
        System.out.println(lxh);
    }
}
