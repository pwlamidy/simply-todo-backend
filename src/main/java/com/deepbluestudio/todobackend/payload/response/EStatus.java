package com.deepbluestudio.todobackend.payload.response;

public enum EStatus {
    SUCCESS("SUCCESS"),
    FAIL("FAIL");

    private final String status;

    EStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
