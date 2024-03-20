package com.ntw.oms.order.queue;

public interface MQProducer {
    void send(String message);
}
