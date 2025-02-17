package com.cbcode.dealertasks.ExceptionsConfig;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
