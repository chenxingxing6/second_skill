package com.lxh.seckill.service;

import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.entity.OrderInfo;
import com.lxh.seckill.entity.SeckillOrder;
import com.lxh.seckill.entity.User;

/**
 * My Blog : www.hfbin.cn
 * github: https://github.com/hfbin
 * Created by: HuangFuBin
 * Date: 2018/7/16
 * Time: 16:46
 * Such description:
 */
public interface SeckillOrderService {

    SeckillOrder getSeckillOrderByUserIdGoodsId(long userId , long goodsId);


    OrderInfo insert(User user , GoodsBo goodsBo);

    OrderInfo getOrderInfo(long orderId);

    long getSeckillResult(Long userId, long goodsId);

    boolean checkPath(User user, long goodsId, String path);

    String createMiaoshaPath(User user, long goodsId);

}
