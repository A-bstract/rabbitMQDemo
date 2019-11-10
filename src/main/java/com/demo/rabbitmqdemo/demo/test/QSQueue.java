package com.demo.rabbitmqdemo.demo.test;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.AMQP.BasicProperties;

public class QSQueue {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String DEFAULT_WORKER_QUEUE = "queue_to_normal_exchange";
    private static final String NORMAL_ROUTE_KEY = "normal_route_key";

    private Connection connection;
    // For consumer and producer schedule.
    private final ExecutorService executorService;

    private static class SimpleThreadFactory implements ThreadFactory {
        private final AtomicInteger idx = new AtomicInteger(0);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread#" + idx.getAndIncrement());
        }
    }

    private static class WorkerConsumer implements Runnable {
        private static final int PREFETCH_COUNTS = 2;
        private final Connection connection;
        private final boolean pushMode;
        private Channel channel;

        private final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            final String message = new String(delivery.getBody(), "UTF-8");
            final Envelope envelope = delivery.getEnvelope();
            final long deliveryTag = envelope.getDeliveryTag();
            final BasicProperties props = delivery.getProperties();
            final String msgId = props.getMessageId();

            System.out.println("Received msg:" + message
                    + " delivery tag:" + deliveryTag
                    + " msg id:" + msgId );
            // Simulate a very time consuming work.
            try {
                Thread.sleep(1000*60*5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.basicAck(deliveryTag, false);
        };

        private void handleGettedMsg(GetResponse getResponse, boolean autoAck) throws IOException {
            final String message = new String(getResponse.getBody(), "UTF-8");
            final Envelope envelope = getResponse.getEnvelope();
            final long deliveryTag = envelope.getDeliveryTag();
            final BasicProperties props = getResponse.getProps();
            final String msgId = props.getMessageId();

            System.out.println("Getted msg:" + message
                    + " delivery tag:" + deliveryTag
                    + " msg id:" + msgId );
            if (!autoAck) {
                channel.basicAck(deliveryTag, false);
            }
        }

        public WorkerConsumer(Connection connection, boolean pushMode) {
            this.connection = connection;
            this.pushMode = pushMode;
        }

        @Override
        public void run() {
            try {
                channel = connection.createChannel();
                if (pushMode) {
                    channel.basicQos(0, PREFETCH_COUNTS, false);
                }
                channel.exchangeDeclare(NORMAL_EXCHANGE, "direct");
                channel.queueDeclare(DEFAULT_WORKER_QUEUE, false, false, true, null);
                channel.queueBind(DEFAULT_WORKER_QUEUE, NORMAL_EXCHANGE, NORMAL_ROUTE_KEY);

                // Push mode
                if (pushMode) {
                    final String tag = channel.basicConsume(DEFAULT_WORKER_QUEUE, false, deliverCallback, consumerTag -> {System.out.println("Cancel consumer:" +consumerTag);});
                    System.out.println("worker consumer tag:" + tag);

                    // Register the second consumer on the channel.
                   /*final String tag2 = channel.basicConsume(DEFAULT_WORKER_QUEUE, false, deliverCallback, consumerTag -> {System.out.println("Cancel consumer:" +consumerTag);});
                   System.out.println("worker consumer#2 tag:" + tag2);*/
                } else { // Pull mode
                    final boolean autoAck = false;
                    while (true) {
                        final GetResponse getResponse = channel.basicGet(DEFAULT_WORKER_QUEUE, autoAck);
                        handleGettedMsg(getResponse, autoAck);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Producer implements Runnable {
        private static final int MSG_COUNTS = 10;
        private final Connection connection;

        public Producer(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                final Channel channel = connection.createChannel();
                channel.exchangeDeclare(NORMAL_EXCHANGE, "direct");
                channel.queueDeclare(DEFAULT_WORKER_QUEUE, false, false, true, null);
                channel.queueBind(DEFAULT_WORKER_QUEUE, NORMAL_EXCHANGE, NORMAL_ROUTE_KEY);

                for (int i = 0; i < MSG_COUNTS; i++) {
                    final BasicProperties.Builder propsBuilder = new BasicProperties.Builder();
                    final BasicProperties props = propsBuilder.appId(Thread.currentThread().toString()).messageId(String.valueOf(i)).build();

                    channel.basicPublish(NORMAL_EXCHANGE, NORMAL_ROUTE_KEY, props, "[normal message.]".getBytes());
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public QSQueue() {
        executorService = Executors.newCachedThreadPool(new SimpleThreadFactory());

        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void execWorkderConsumer(boolean pushMode) {
        executorService.submit(new WorkerConsumer(connection, pushMode));
    }

    public void execProducer(int n) {
        for (int i = 0; i < n; i++) {
            executorService.submit(new Producer(connection));
        }
    }

    public void clearConfig(String[] queues) {
        try {
            final Channel channel = connection.createChannel();
            channel.exchangeDelete(NORMAL_EXCHANGE);
            channel.queueDelete(DEFAULT_WORKER_QUEUE);
            for (String queue : queues) {
                channel.queueDelete(queue);
            }
            System.out.println("Config cleared.");
            System.exit(0);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        System.out.println("QS queue.");
        if (args.length > 0) {
            final String which = args[0];
            if ("consumer".equals(which)) {
                final boolean pushMode = (args.length > 1 ? "push".equals(args[1]) : true);
                new QSQueue().execWorkderConsumer(pushMode);
            } else if ("producer".equals(which)) {
                final int n = (args.length > 1 ? Integer.parseInt(args[1]) : 1);
                new QSQueue().execProducer(n);
            } else if ("clearconfig".equals(which)) {
                String[] queues = new String[0];
                if (args.length > 1) {
                    queues = new String[args.length - 1];
                    System.arraycopy(args, 1, queues, 0, args.length - 1);
                }
                new QSQueue().clearConfig(queues);
            }
        }
    }
}
