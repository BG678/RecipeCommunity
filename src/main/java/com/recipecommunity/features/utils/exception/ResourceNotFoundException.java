package com.recipecommunity.features.utils.exception;


public class ResourceNotFoundException extends RuntimeException {
    private String message;

    public ResourceNotFoundException() {
        message = "Requested resource could not be found";
    }

    public ResourceNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
