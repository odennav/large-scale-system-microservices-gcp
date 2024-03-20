package com.ntw.oms.order.queue.local;

import java.util.concurrent.LinkedBlockingQueue;

public class LocalMQ extends LinkedBlockingQueue<String> {

    private static LocalMQ messageQueue = null;

    private LocalMQ() {
    }

    public static LocalMQ getInstance() {
        if (messageQueue == null) {
            synchronized (LocalMQ.class) {
                if (messageQueue == null) {
                    messageQueue = new LocalMQ();
                }
            }
        }
        return messageQueue;
    }

    public boolean addToQueue(String message) {
        return this.offer(message);
    }

    public String getFromQueue() {
        return this.poll();
    }

}