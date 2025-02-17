package com.cbcode.dealertasks.ExceptionsConfig;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
