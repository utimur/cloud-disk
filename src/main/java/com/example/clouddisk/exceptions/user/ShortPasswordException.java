package com.example.clouddisk.exceptions.user;

public class ShortPasswordException extends RuntimeException{
    public ShortPasswordException(String message) {
        super(message);
    }
}
