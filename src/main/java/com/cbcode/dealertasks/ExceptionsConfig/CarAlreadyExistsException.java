package com.cbcode.dealertasks.ExceptionsConfig;

public class CarAlreadyExistsException extends RuntimeException {
    public CarAlreadyExistsException(String message) {
        super(message);
    }
}
