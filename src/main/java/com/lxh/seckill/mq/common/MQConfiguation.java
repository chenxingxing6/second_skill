package com.lxh.seckill.mq.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
@ConditionalOnProperty(prefix = "rocketmq", value = "isEnable", havingValue = "true")
public class MQConfiguation {
	private Logger log = LoggerFactory.getLogger(MQConfiguation.class);
	private MQProperties mqProperties;
	private ApplicationContext applicationContext;

	public MQConfiguation(MQProperties mqProperties, ApplicationContext applicationContext) {
		this.mqProperties = mqProperties;
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void init() {
		// 前置校验
		Assert.isTrue(mqProperties.getProducerGroup().startsWith("p_") && mqProperties.getProducerGroup().contains(mqProperties.getTopic()),
				"生产者不符合规范！producerGroup:" + mqProperties.getProducerGroup() + ",topic:" + mqProperties.getTopic());
		Assert.isTrue(mqProperties.getConsumerGroup().startsWith("c_") && mqProperties.getConsumerGroup().contains(mqProperties.getTopic()),
				"消费者不符合规范！consumerGroup:" + mqProperties.getConsumerGroup() + ",topic:" + mqProperties.getTopic());

		// 初始化消费者
		Map<String, AbstractRocketConsumer> consumers = applicationContext.getBeansOfType(AbstractRocketConsumer.class);
		if (consumers == null || consumers.size() == 0) {
			log.info("init rocket consumer 0");
		}
		Iterator<String> beans = consumers.keySet().iterator();
		while (beans.hasNext()) {
			String beanName = (String) beans.next();
			AbstractRocketConsumer consumer = consumers.get(beanName);
			consumer.init();
			createConsumer(consumer);
			log.info("RocketMq消费者初始化成功！");
		}
	}

	/**
	 * 通过消费者信心创建消费者
	 */
	public void createConsumer(AbstractRocketConsumer arc) {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqProperties.getGroupName());
		consumer.setNamesrvAddr(mqProperties.getNamesrvAddr());
		consumer.setConsumeThreadMin(mqProperties.getConsumerConsumeThreadMin());
		consumer.setConsumeThreadMax(mqProperties.getConsumerConsumeThreadMax());
		consumer.registerMessageListener(arc.messageListener);
		consumer.setConsumeMessageBatchMaxSize(mqProperties.getConsumerConsumeMessageBatchMaxSize());
		try {
			consumer.subscribe(arc.topics, arc.tags);
			consumer.start();
			arc.mqPushConsumer=consumer;
		} catch (MQClientException e) {
			log.error("info consumer title {}", arc.consumerTitle, e);
		}
	}

	/**
	 * 注入一个默认的消费者
	 */
	@Bean
	public DefaultMQProducer getRocketMQProducer() throws MQClientException {
		DefaultMQProducer sendProducer;
		if (StringUtils.isEmpty(mqProperties.getGroupName())) {
			throw new MQClientException(-1, "groupName is blank");
		}
		if (StringUtils.isEmpty(mqProperties.getNamesrvAddr())) {
			throw new MQClientException(-1, "nameServerAddr is blank");
		}
		sendProducer = new DefaultMQProducer(mqProperties.getGroupName());
		sendProducer.setNamesrvAddr(mqProperties.getNamesrvAddr());
		sendProducer.setMaxMessageSize(mqProperties.getProducerMaxMessageSize());
		sendProducer.setSendMsgTimeout(mqProperties.getProducerSendMsgTimeout());
		sendProducer.setRetryTimesWhenSendFailed(mqProperties.getProducerRetryTimesWhenSendFailed());

		try {
			sendProducer.start();
			log.info("producer is start ! groupName:{},namesrvAddr:{}", mqProperties.getGroupName(), mqProperties.getNamesrvAddr());
		} catch (MQClientException e) {
			log.error(String.format("producer is error {}", e.getMessage(), e));
			throw e;
		}
		return sendProducer;
	}

	public MQProperties getMqProperties() {
		return mqProperties;
	}

	public void setMqProperties(MQProperties mqProperties) {
		this.mqProperties = mqProperties;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
