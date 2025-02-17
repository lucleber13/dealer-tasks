package com.cbcode.dealertasks.ExceptionsConfig;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String invalidCredentials) {
        super(invalidCredentials);
    }
}
