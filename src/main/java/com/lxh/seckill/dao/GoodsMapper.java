package com.lxh.seckill.dao;

import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.entity.Goods;

import java.util.List;

public interface GoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    List<GoodsBo> selectAllGoodes ();

    GoodsBo getseckillGoodsBoByGoodsId(long goodsId);

    int updateStock(long goodsId);
}
