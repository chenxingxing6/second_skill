package com.lxh.seckill.redis;

public class GoodsKey extends BasePrefix{

	private GoodsKey(String prefix) {
		super(prefix);
	}
	// html页面
	public static GoodsKey getGoodsList = new GoodsKey("gl");
	// 商品详情
	public static GoodsKey getGoodsDetail = new GoodsKey("gd");
	// 库存
	public static GoodsKey getSeckillGoodsStock= new GoodsKey( "gs");
}
