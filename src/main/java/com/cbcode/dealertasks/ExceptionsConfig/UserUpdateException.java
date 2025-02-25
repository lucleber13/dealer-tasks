package com.cbcode.dealertasks.ExceptionsConfig;

import org.springframework.dao.DataAccessException;

public class UserUpdateException extends RuntimeException {
    public UserUpdateException(String s, DataAccessException e) {
        super(s, e);
    }
}
