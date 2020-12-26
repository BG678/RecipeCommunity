package com.recipecommunity.utils;

public class PageDoesNotExist extends RuntimeException {
    private String message;

    public PageDoesNotExist() {
        message = "This page doesn't exist";
    }

    public PageDoesNotExist(String message) {
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
