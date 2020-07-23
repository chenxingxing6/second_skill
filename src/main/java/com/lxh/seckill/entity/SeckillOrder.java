package com.lxh.seckill.entity;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder {
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;

}
