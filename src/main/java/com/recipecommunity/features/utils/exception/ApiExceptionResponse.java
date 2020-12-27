package com.recipecommunity.features.utils.exception;

import java.io.Serializable;
import java.time.Instant;

public class ApiExceptionResponse implements Serializable {
    private static final long serialVersionUID = 770213465451L;
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;

    public ApiExceptionResponse(String error, String message, Integer status) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ApiExceptionResponse() {

    }

    public String getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
