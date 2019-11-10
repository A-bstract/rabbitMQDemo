package com.demo.rabbitmqdemo.demo.test;

import com.demo.rabbitmqdemo.demo.env.Constans;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class Consumer1 {

    /*@RabbitListener(queues = Constans.QUEUE_LOG_OPER,containerFactory = "LISTENER_OPER_LOG")
    public void operLogConsumer(Message message,
                                Channel channel) throws IOException, InterruptedException {
        log.info("============开始==========");
        channel.basicQos(5);
        Thread.sleep(13000);
        // ACK 消息确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }*/

}
