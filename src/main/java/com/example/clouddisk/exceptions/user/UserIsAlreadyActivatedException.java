package com.example.clouddisk.exceptions.user;

public class UserIsAlreadyActivatedException extends RuntimeException {
    public UserIsAlreadyActivatedException(String message) {
        super(message);
    }
    public static String getException() {
        return "UserIsAlreadyActivatedException";
    }
}
