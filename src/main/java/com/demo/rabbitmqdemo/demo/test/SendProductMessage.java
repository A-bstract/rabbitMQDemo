package com.demo.rabbitmqdemo.demo.test;

import com.demo.rabbitmqdemo.demo.env.Constans;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SendProductMessage {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/test/productTest")
    public void productTest(){
        rabbitTemplate.setExchange(Constans.EXCHANGE_DIRECT_LOG_EXCHANGE);
        rabbitTemplate.setRoutingKey(Constans.LOG_BIND_KEY);
        String message = "Hellow Word!";
        MessageBuilder messageBuilder = MessageBuilder.withBody(message.getBytes());
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 10000; i++) {
            executorService.submit(() -> {
                rabbitTemplate.convertAndSend(messageBuilder.build());
            });
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
