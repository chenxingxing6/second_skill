package com.lxh.seckill.common;

/**
 * created by lanxinghua@2dfire.com on 2020/7/25
 */

public enum LockTypeEnum {
    REFRESH_TOKEN("REFRESH_TOKEN", "刷新令牌");

    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;

    LockTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
