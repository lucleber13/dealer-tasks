package com.cbcode.dealertasks.ExceptionsConfig;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String message) {
        super(message);
    }
}
