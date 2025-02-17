package com.cbcode.dealertasks.ExceptionsConfig;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String s) {
        super(s);
    }
}
