package com.ntw.oms.order.queue;

import com.ntw.oms.order.entity.Order;

import java.util.HashMap;

public class MQOrder {
    private Order order;
    private String authHeader;
    private HashMap<String, String> tracingContextMap;

    public MQOrder() {
        this.tracingContextMap = new HashMap<>();
    }

    public MQOrder(Order order, String authHeader) {
        this.order = order;
        this.authHeader = authHeader;
        this.tracingContextMap = new HashMap<>();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public HashMap<String, String> getTracingContextMap() {
        return tracingContextMap;
    }

    public void setTracingContextMap(HashMap<String, String> tracingContextMap) {
        this.tracingContextMap = tracingContextMap;
    }

    @Override
    public String toString() {
        return "{" +
                "\"order\":" + (order == null ? "null" : order) + ", " +
                "\"authHeader\":" + (authHeader == null ? "null" : "\"" + authHeader + "\"") + ", " +
                "\"tracingContextMap\":" + (tracingContextMap == null ? "null" : "\"" + tracingContextMap + "\"") +
                "}";
    }
}
