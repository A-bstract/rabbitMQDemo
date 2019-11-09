package com.demo.rabbitmqdemo.demo.mqdemo;

import com.demo.rabbitmqdemo.demo.env.Constans;
import org.junit.Test;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductDemo implements RabbitTemplate.ConfirmCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testProduct(){
        rabbitTemplate.setExchange(Constans.EXCHANGE_DIRECT_LOG_EXCHANGE);
        rabbitTemplate.setRoutingKey(Constans.LOG_BIND_KEY);
        String message = "Hellow Word!";
        MessageBuilder messageBuilder = MessageBuilder.withBody(message.getBytes());
        rabbitTemplate.convertAndSend(messageBuilder.build());
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {

    }
}
