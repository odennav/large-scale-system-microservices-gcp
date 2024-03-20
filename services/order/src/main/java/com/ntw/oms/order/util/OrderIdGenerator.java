package com.ntw.oms.order.util;

public class OrderIdGenerator {

    public static final String ref = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encodeToBase36(long num) {
        if (num < 0) {
            num = -num;
        }
        String result = "";
        while (num > 0) {
            result = ref.charAt((int) (num%36)) + result;
            num /= 36;
        }
        return result;
    }

    public static String createOrderId() {
        long uniqueId = UniqueIdGenerator.getInstance().nextId();
        return encodeToBase36(uniqueId);
    }

}
