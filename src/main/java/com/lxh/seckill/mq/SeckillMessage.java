package com.lxh.seckill.mq;


import com.lxh.seckill.entity.User;
import com.lxh.seckill.mq.common.AsyncMsg;

public class SeckillMessage extends AsyncMsg {
	private User user;
	private long goodsId;
	private Long time;


	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
