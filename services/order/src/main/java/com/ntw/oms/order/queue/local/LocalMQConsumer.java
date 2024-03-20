package com.ntw.oms.order.queue.local;

import com.ntw.oms.order.queue.MQConsumer;
import com.ntw.oms.order.queue.OrderConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalMQConsumer implements MQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LocalMQConsumer.class);

    private OrderConsumer orderConsumer;

    public OrderConsumer getOrderConsumer() {
        return orderConsumer;
    }

    @Override
    public void setOrderConsumer(OrderConsumer orderProcessor) {
        this.orderConsumer = orderProcessor;
    }

    @Override
    public void startConsumer() {
        (new Thread(new LocalMQConsumerPoller())).start();
    }

    public void processMessage(String message) {
        logger.debug("Received message from order queue: message={}", message);
        getOrderConsumer().processOrder(message);
    }

    class LocalMQConsumerPoller implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("LocalMQProcessor sleep interrupted: {}", e);
                }
                String message = LocalMQ.getInstance().getFromQueue();
                if (message != null) {
                    processMessage(message);
                }
            }
        }
    }
}