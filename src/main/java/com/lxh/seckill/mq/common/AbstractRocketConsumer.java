package com.lxh.seckill.mq.common;

import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;

/**
 * created by lanxinghua@2dfire.com on 2020/7/21
 */
public abstract class AbstractRocketConsumer implements ConsumerCallBack{
    protected String topics;
    protected String tags;
    protected MessageListener messageListener;
    protected String consumerTitle;
    protected MQPushConsumer mqPushConsumer;

    public void config(String topics, String tags, String title) {
        this.topics = topics;
        this.tags = tags;
        this.consumerTitle = title;
    }

    @Override
    public void init() {

    }

    @Override
    public void registerMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
