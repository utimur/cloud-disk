package com.example.clouddisk.exceptions.user;

public class EmptyPasswordException extends RuntimeException {
    public EmptyPasswordException(String message) {
        super(message);
    }
}
