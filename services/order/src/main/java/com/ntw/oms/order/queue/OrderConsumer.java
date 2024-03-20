package com.ntw.oms.order.queue;

import com.google.gson.Gson;
import com.ntw.oms.order.processor.OrderPostProcessor;
import com.ntw.oms.order.service.OrderServiceImpl;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    OrderPostProcessor orderProcessor;

    public OrderPostProcessor getOrderProcessor() {
        return orderProcessor;
    }

    public void setOrderProcessor(OrderPostProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    public MQOrder getOrder(String serializedOrderString) {
        return (new Gson()).fromJson(serializedOrderString, MQOrder.class);
    }

    public boolean processOrder(String orderJSON) {
        MQOrder mqOrder = getOrder(orderJSON);
        if (mqOrder == null) {
            logger.error("Unable to deserialize order received for the order queue.");
            return false;
        }
        return processOrder(mqOrder);
    }

    private boolean processOrder(MQOrder mqOrder) {
        // reserve inventory
        HashMap<String, String> contextMap = mqOrder.getTracingContextMap();
        Tracer tracer = GlobalTracer.get();
        SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapAdapter(contextMap));
        Span span = tracer.buildSpan("orderProcessing").asChildOf(spanContext).start();
        tracer.activateSpan(span);
        OrderServiceImpl.getThreadLocal().set(mqOrder.getAuthHeader());
        boolean success=true;
        if (! getOrderProcessor().processOrder(mqOrder.getOrder())) {
            logger.error("Unable to reserve inventory for order; context={}", mqOrder);
            success = false;
        }
        logger.info("Processed order: {}", mqOrder.getOrder());
        span.finish();
        return success;
    }


}
