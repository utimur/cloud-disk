package com.example.clouddisk.exceptions.user;

public class MailAlreadyExist extends RuntimeException {
    public MailAlreadyExist(String message) {
        super(message);
    }
}
