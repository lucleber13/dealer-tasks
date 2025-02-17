package com.cbcode.dealertasks.Users.service;

public interface EmailService {

    void sendPasswordResetEmail(String toEmail, String resetLink);
}
