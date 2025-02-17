package com.cbcode.dealertasks.ExceptionsConfig;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String noUsersFound) {
        super(noUsersFound);
    }
}
