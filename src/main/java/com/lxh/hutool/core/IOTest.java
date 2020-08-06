package com.lxh.hutool.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.Tailer;

import java.io.FileWriter;

/**
 * created by lanxinghua@2dfire.com on 2020/8/5
 * IoUtil 流操作工具类
 * FileUtil 文件读写和操作的工具类。
 * FileTypeUtil 文件类型判断工具类
 * WatchMonitor 目录、文件监听，封装了JDK1.7中的WatchService
 * ClassPathResource针对ClassPath中资源的访问封装
 * FileReader 封装文件读取
 * FileWriter 封装文件写入
 */
public class IOTest {
    public static void main(String[] args) throws Exception{
        FileWriter writer = new FileWriter("lxh.txt");
        writer.write("hello world");
        writer.close();

        // 实时打印文件日志
        Tailer tailer = new Tailer(FileUtil.file("F:\\IdeaProjects\\study\\second_skill\\lxh.txt"), Tailer.CONSOLE_HANDLER, 2);
        tailer.start();
    }
}
