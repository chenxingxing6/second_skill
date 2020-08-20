package com.lxh.hutool.excel;

import lombok.Data;

import java.io.Serializable;

/**
 * created by lanxinghua@2dfire.com on 2020/8/20
 */
@Data
public class FillData implements Serializable {
    private String name;
    private double number;
}
