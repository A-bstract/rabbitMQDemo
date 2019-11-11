package com.demo.rabbitmqdemo.demo.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基本配置
 */
@Configuration
@Slf4j
public class BaseConfig {


    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean(name = "connectionFactory")
    public ConnectionFactory connectionFactory(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        return connectionFactory;
    }
}
