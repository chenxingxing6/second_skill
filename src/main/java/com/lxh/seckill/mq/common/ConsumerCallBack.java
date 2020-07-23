package com.lxh.seckill.mq.common;


import org.apache.rocketmq.client.consumer.listener.MessageListener;

/**
 * created by lanxinghua@2dfire.com on 2020/7/21
 */
public interface ConsumerCallBack {
    /**
     * 初始化消费者
     */
    public abstract void init();

    /**
     * 注册监听
     * @param messageListener
     */
    public void registerMessageListener(MessageListener messageListener);
}
