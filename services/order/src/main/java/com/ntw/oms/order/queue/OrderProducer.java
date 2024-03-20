package com.ntw.oms.order.queue;

import com.google.gson.Gson;
import com.ntw.oms.order.entity.Order;
import com.ntw.oms.order.service.OrderServiceImpl;
import io.opentracing.Span;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OrderProducer {
    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    @Autowired
    private MQProducer mqProducer;

    public MQProducer getMessageQueue() {
        return mqProducer;
    }

    public void setMessageQueue(MQProducer MQProducer) {
        this.mqProducer = MQProducer;
    }

    public void queueOrder(Order order) throws Exception {
        String authHeader = OrderServiceImpl.getThreadLocal().get();
        Span span = GlobalTracer.get().activeSpan();
        HashMap<String, String> contextMap = new HashMap<>();
        GlobalTracer.get().inject(span.context(), Format.Builtin.TEXT_MAP, new TextMapAdapter(contextMap));
        MQOrder mqOrder = new MQOrder(order, authHeader);
        mqOrder.setTracingContextMap(contextMap);
        String message = (new Gson()).toJson(mqOrder);
        try {
            mqProducer.send(message);
            logger.debug("Published order to message queue; order={}", mqOrder);
        } catch (Exception e) {
            logger.error("Exception publishing order to queue", e);
            throw e;
        }
    }
}
