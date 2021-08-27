package com.sylleryum.spotifyhelper.model;

public class ApiErrorEntity {
    private String errorMessage;
    private String traceId;

    public ApiErrorEntity() {
    }

    public ApiErrorEntity(String errorMessage, String traceId) {
        this.errorMessage = errorMessage;
        this.traceId = traceId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
