package com.lxh.hutool.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.word.Word07Writer;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * created by lanxinghua@2dfire.com on 2020/8/6
 */
public class ExcelWriteTest {
    public static void main(String[] args) {
        // createXlsx();
        // reateWord();
        createBigXlsx();
    }

    private static void createWord(){
        Word07Writer writer = new Word07Writer();
        // 添加段落（标题）
        writer.addText(new Font("方正小标宋简体", Font.PLAIN, 22), "我是第一部分", "我是第二部分");
        // 添加段落（正文）
        writer.addText(new Font("宋体", Font.PLAIN, 22), "我是正文第一部分", "我是正文第二部分");
        // 写出到文件
        writer.flush(FileUtil.file("f:/wordWrite.docx"));
        // 关闭
        writer.close();
    }


    private static void createXlsx(){
        List<List<String>> rows = getData();
        ExcelWriter writer = ExcelUtil.getWriter(new File("lxh.xlsx"));
        writer.passCurrentRow();
        writer.merge(2, "测试标题");
        writer.write(rows, true);
        writer.close();
    }


    // 100万数据
    private static void createBigXlsx(){
        List<List<String>> rows = getData();
        ExcelWriter writer = ExcelUtil.getBigWriter(new File("lxh.xlsx"));
        writer.merge(1, "测试标题");
        writer.write(rows, true);
        writer.close();
    }



    private static List<List<String>> getData(){
        List<List<String>> result = CollUtil.newArrayList();
        for (int i = 0; i < 10; i++) {
            List<String> row1 = CollUtil.newArrayList("a"+i, "b"+i);
            result.add(row1);
        }
        return result;
    }
}
