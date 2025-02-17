package com.cbcode.dealertasks.ExceptionsConfig;

public class PasswordTooShortException extends RuntimeException {
    public PasswordTooShortException(String s) {
        super(s);
    }
}
