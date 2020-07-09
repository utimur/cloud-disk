package com.example.clouddisk.exceptions.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public static String getException() {
        return "UserNotFoundException";
    }
}
