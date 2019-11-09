package com.demo.rabbitmqdemo.demo.config.consumer;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {

    @Bean(name="LISTENER_OPER_LOG")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> listenerTest(ConnectionFactory connectionFactory){
        //消息的统一过滤器
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);//并发 消费者数量
        factory.setMaxConcurrentConsumers(10);//允许单个消费者 最大的消费数量
        factory.setReceiveTimeout(10000L);//10秒
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置手动提交
        return  factory;
    }
}
