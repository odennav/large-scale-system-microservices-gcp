package com.ntw.oms.order.queue.local;

import com.ntw.oms.order.queue.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalMQProducer implements MQProducer {

    private static final Logger logger = LoggerFactory.getLogger(LocalMQProducer.class);

    private static LocalMQProducer messageQueue = new LocalMQProducer();

    @Override
    public void send(String message) {
        LocalMQ queue = LocalMQ.getInstance();
        if (!queue.offer(message)) {
            logger.error("Unable to insert message into queue: message={}", message);
        }
    }

}