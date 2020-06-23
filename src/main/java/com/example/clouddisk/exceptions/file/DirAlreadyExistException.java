package com.example.clouddisk.exceptions.file;

public class DirAlreadyExistException extends RuntimeException {
    public DirAlreadyExistException(String message) {
        super(message);
    }
}
