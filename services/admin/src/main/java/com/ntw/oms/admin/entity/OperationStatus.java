package com.ntw.oms.admin.entity;

public class OperationStatus {
    private boolean success;
    private String message;

    public OperationStatus() {
        success = false;
        message = "Not performed";
    }

    public OperationStatus(boolean success) {
        this();
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "\"success\":\"" + success + "\"" + ", " +
                "\"message\":" + (message == null ? "null" : "\"" + message + "\"") +
                "}";
    }
}
