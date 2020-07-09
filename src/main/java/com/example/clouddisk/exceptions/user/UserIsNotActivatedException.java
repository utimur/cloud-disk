package com.example.clouddisk.exceptions.user;

public class UserIsNotActivatedException extends RuntimeException {

    public UserIsNotActivatedException(String message) {
        super(message);
    }

    public static String getException() {
        return "UserIsNotActivatedException";
    }
}
