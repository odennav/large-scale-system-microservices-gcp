package com.ntw.oms.order.queue.rabbitmq;

import com.ntw.oms.order.queue.MQProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer implements MQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private String queueName;
    private Connection connection;
    private Channel channel;

    public RabbitMQProducer(String hostName, String queueName) throws IOException, TimeoutException {
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
    public void send(String message) {
        Tracer tracer = GlobalTracer.get();
        Span queueOrderSpan = tracer.buildSpan("publishOrderToMQ").asChildOf(tracer.activeSpan()).start();
        try {
            channel.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        queueOrderSpan.finish();
        logger.debug("Published message queue; order={}", message);
    }

}
