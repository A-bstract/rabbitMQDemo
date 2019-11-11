package com.demo.rabbitmqdemo.demo.mqdemo;

import com.demo.rabbitmqdemo.demo.env.Constans;
import com.rabbitmq.client.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerDemo {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        //channel.queueDeclare(Constans.QUEUE_LOG_OPER,true,false,false,null);

        channel.basicQos(0, 2, false);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            System.out.println("开始消费");
            final String message = new String(delivery.getBody(), "UTF-8");
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


        Lock lock = new ReentrantLock();
        lock.lock();
        Condition condition = lock.newCondition();
        condition.await();
    }
}
