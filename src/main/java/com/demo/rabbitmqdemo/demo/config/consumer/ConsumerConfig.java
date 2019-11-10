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
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置手动提交
        factory.setConcurrentConsumers(1);//一个客户端 初始 起多少个channel
        factory.setMaxConcurrentConsumers(1);//一个客户端 最多 起多少个channel
        factory.setReceiveTimeout(100000L);//10秒
        return  factory;
    }
}
