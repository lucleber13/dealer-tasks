package com.cbcode.dealertasks.ExceptionsConfig;

public class NotAuthorizedAccessException extends RuntimeException {
    public NotAuthorizedAccessException(String userNotAuthenticated) {
        super(userNotAuthenticated);
    }
}
