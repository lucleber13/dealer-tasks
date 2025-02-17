package com.cbcode.dealertasks.ExceptionsConfig;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String s) {
        super(s);
    }
}
