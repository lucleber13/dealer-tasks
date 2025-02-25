package com.cbcode.dealertasks.ExceptionsConfig;

import org.springframework.dao.DataAccessException;

public class UserDeletionException extends RuntimeException {
    public UserDeletionException(String s, DataAccessException e) {
        super(s, e);
    }
}
