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

   /* @RabbitListener(queues = Constans.QUEUE_LOG_OPER,containerFactory = "LISTENER_OPER_LOG")
    public void operLogConsumer(Message message, @Headers Map<String, Object> headers,
                                Channel channel) throws IOException, InterruptedException {
        channel.basicQos(1);
        Thread.sleep(2000);
        // ACK
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);

        //channel.txRollback();
        log.info("1号消费者：" + new String(message.getBody()));
    }*/

}
