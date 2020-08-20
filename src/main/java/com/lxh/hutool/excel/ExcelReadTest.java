package com.lxh.hutool.excel;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Validator;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * created by lanxinghua@2dfire.com on 2020/8/6
 */
public class ExcelReadTest {
    public static void main(String[] args) {
        ExcelReader reader = ExcelUtil.getReader(new File("lxh.xlsx"));
        List<List<Object>> read = reader.read(1);
        for (List<Object> objects : read) {
            for (Object object : objects) {
                System.out.print(object +" ");
            }
            System.out.println();
        }
    }
}
