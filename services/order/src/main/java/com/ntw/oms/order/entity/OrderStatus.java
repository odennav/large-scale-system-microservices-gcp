package com.ntw.oms.order.entity;

public enum OrderStatus {
    IN_PROCESS("In Process"), CREATED("Created");
    private String status;

    OrderStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static OrderStatus getStatus(String statusString) {
        if (statusString.equals(OrderStatus.IN_PROCESS.toString())) {
            return OrderStatus.IN_PROCESS;
        }
        else if (statusString.equals(OrderStatus.CREATED.toString())) {
            return OrderStatus.CREATED;
        }
        return null;
    }
}
