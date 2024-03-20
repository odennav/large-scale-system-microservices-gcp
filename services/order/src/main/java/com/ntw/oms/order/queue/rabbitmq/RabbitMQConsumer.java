package com.ntw.oms.order.queue.rabbitmq;

import com.ntw.oms.order.queue.MQConsumer;
import com.ntw.oms.order.queue.OrderConsumer;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMQCallback implements DeliverCallback {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQCallback.class);
    private OrderConsumer orderConsumer;

    public void setOrderConsumer(OrderConsumer orderConsumer) {
        this.orderConsumer = orderConsumer;
    }

    @Override
    public void handle(String s, Delivery delivery) throws IOException {
        String message = new String(delivery.getBody(), "UTF-8");
        logger.debug("Received message from order queue: message={}", message);
        orderConsumer.processOrder(message);
    }

}

public class RabbitMQConsumer implements MQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private String queueName;
    private Connection connection;
    private Channel channel;

    private OrderConsumer orderConsumer;

    public OrderConsumer getOrderConsumer() {
        return orderConsumer;
    }

    @Override
    public void setOrderConsumer(OrderConsumer orderProcessor) {
        this.orderConsumer = orderProcessor;
    }

    public RabbitMQConsumer(String hostName, String queueName)
            throws IOException, TimeoutException {
        this.queueName = queueName;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostName);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
        } catch (Exception e) {
            logger.error("Exception publishing order to queue", e);
            throw e;
        }
    }

    @Override
    public void startConsumer() throws IOException {
        RabbitMQCallback callback = new RabbitMQCallback();
        callback.setOrderConsumer(getOrderConsumer());
        try {
            logger.info("Waiting for messages.");
            channel.basicConsume(queueName, true, callback, consumerTag -> {});
        } catch (Exception e) {
            logger.error("Unable to initialize order queue: ", e);
            throw e;
        }
    }

}
