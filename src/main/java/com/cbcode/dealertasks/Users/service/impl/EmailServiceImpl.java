package com.cbcode.dealertasks.Users.service.impl;

import com.cbcode.dealertasks.Users.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a password reset email to the user.
     * @param toEmail the email address of the user
     * @param resetLink the link to reset the password
     */
    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n\n" +
                    "https://yourdomain.com/reset-password?token=" + resetLink + "\n\n" +
                    "If you didn't request a password reset, please ignore this email.\n\n" +
                    "This link will expire in 30 minutes.");
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send email " ,e);
        }
    }
}
