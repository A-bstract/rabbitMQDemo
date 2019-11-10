package com.demo.rabbitmqdemo.demo.test;

import com.demo.rabbitmqdemo.demo.env.Constans;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;
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

        log.info("============开始==========");
        channel.basicQos(2);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            final Envelope envelope = delivery.getEnvelope();
            final long deliveryTag = envelope.getDeliveryTag();
            final AMQP.BasicProperties props = delivery.getProperties();
            final String msgId = props.getMessageId();

            System.out.println("Received msg:" + message
                    + " delivery tag:" + deliveryTag
                    + " msg id:" + msgId );
            // Simulate a very time consuming work.
            try {
                Thread.sleep(13000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.basicAck(deliveryTag, false);
        };

        channel.basicConsume(Constans.QUEUE_LOG_OPER, false, deliverCallback, consumerTag -> {System.out.println("Cancel consumer:" +consumerTag);});
        Thread.sleep(13000);
        // ACK 消息确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
