package com.cbcode.dealertasks.ExceptionsConfig;

import org.modelmapper.MappingException;

public class UserMappingException extends RuntimeException {
    public UserMappingException(String s, MappingException e) {
        super(s, e);
    }
}
