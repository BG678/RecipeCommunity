package com.recipecommunity.features.jwt;

public class UsernameIsAlreadyTakenException extends RuntimeException {
    private String message;

    public UsernameIsAlreadyTakenException() {
        message = "Username is already taken";
    }

    public UsernameIsAlreadyTakenException(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
