package com.cbcode.dealertasks.ExceptionsConfig;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
