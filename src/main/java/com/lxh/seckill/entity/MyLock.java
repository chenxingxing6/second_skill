package com.lxh.seckill.entity;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyLock {
    private Integer id;

    private Date gmtModify;

    private String lockDesc;

    private String lockType;

    private Long version;
}
