package com.cbcode.dealertasks.ExceptionsConfig;

public class EmailNotMatchException extends RuntimeException {
    public EmailNotMatchException(String s) {
        super(s);
    }
}
