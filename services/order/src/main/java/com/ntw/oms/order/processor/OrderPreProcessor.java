package com.ntw.oms.order.processor;

import com.ntw.oms.order.entity.Order;
import com.ntw.oms.order.queue.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderPreProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderPreProcessor.class);

    @Autowired
    private OrderProducer orderProducer;

    public boolean queueOrder(Order order) {
        try {
            orderProducer.queueOrder(order);
        } catch (Exception e) {
            logger.error("Unable to queue order", e);
            return false;
        }
        return true;
    }

}
