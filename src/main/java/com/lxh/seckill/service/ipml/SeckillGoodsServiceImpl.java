package com.lxh.seckill.service.ipml;

import com.lxh.seckill.bo.GoodsBo;
import com.lxh.seckill.dao.GoodsMapper;
import com.lxh.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by: HuangFuBin
 * Date: 2018/7/12
 * Time: 19:47
 * Such description:
 */

@Service("seckillGoodsService")
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Override
    public List<GoodsBo> getSeckillGoodsList() {
        return goodsMapper.selectAllGoodes();
    }

    @Override
    public GoodsBo getseckillGoodsBoByGoodsId(long goodsId) {
        return goodsMapper.getseckillGoodsBoByGoodsId(goodsId);
    }

    @Override
    public int reduceStock(long goodsId) {
        return goodsMapper.updateStock(goodsId);
    }
}
