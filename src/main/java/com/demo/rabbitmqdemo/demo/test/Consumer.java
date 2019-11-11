package com.demo.rabbitmqdemo.demo.test;

import com.demo.rabbitmqdemo.demo.env.Constans;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Consumer {

    @RabbitListener(queues = Constans.QUEUE_LOG_OPER,containerFactory = "LISTENER_OPER_LOG")
    public void operLogConsumer(Message message,
                                Channel channel) throws IOException, InterruptedException {
        System.out.println(new String(message.getBody()));
        Thread.sleep(3000);
        // ACK 消息确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
