package com.ntw.oms.order.queue;

public interface MQConsumer {
    void startConsumer() throws Exception;
    void setOrderConsumer(OrderConsumer orderProcessor);
}
