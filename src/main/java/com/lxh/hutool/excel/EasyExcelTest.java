package com.lxh.hutool.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by lanxinghua@2dfire.com on 2020/8/20
 * 模板用{}表示你要用的变量，本来就有{,}特殊字符需要用"\{","\}\"代替
 * {}代表普通变量
 * {.}代表list变量
 */
public class EasyExcelTest {
    public static void main(String[] args) {
        String templateFileName = "F:\\IdeaProjects\\study\\second_skill\\src\\main\\resources\\excel\\template.xls";
        String fileName = System.currentTimeMillis() + ".xls";
        ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(data(), fillConfig, writeSheet);
        excelWriter.fill(data(), fillConfig, writeSheet);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "2019年10月9日13:28:28");
        map.put("total", 10);
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
    }

    private static List<FillData> data(){
        List<FillData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FillData data = new FillData();
            data.setName(i+"name");
            data.setNumber(i);
            list.add(data);
        }
        return list;
    }
}
