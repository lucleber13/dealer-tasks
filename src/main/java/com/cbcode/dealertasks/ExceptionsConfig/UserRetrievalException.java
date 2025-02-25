package com.cbcode.dealertasks.ExceptionsConfig;

import org.springframework.dao.DataAccessException;

public class UserRetrievalException extends RuntimeException {
    public UserRetrievalException(String s, DataAccessException e) {
        super(s, e);
    }
}
