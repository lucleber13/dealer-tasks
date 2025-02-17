package com.cbcode.dealertasks.Users.security.DTOs.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public record SignInRequest(String email, String password) {

    private static final Logger logger = LoggerFactory.getLogger(SignInRequest.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    public SignInRequest {
        validateEmail(email);
        validatePassword(password);
    }

    /**
     * Validates the email of the user.
     * The email cannot be null or empty and must be a valid email format.
     * @param email - The email of the user.
     * @throws IllegalArgumentException if the email is null, empty or invalid.
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            logger.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            logger.error("Invalid email format: {}", email);
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates the password of the user.
     * The password cannot be null or empty and must be at least eight characters long.
     * @param password - The password of the user.
     * @throws IllegalArgumentException if the password is null, empty or less than eight characters long.
     */
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            logger.error("Password cannot be null or empty");
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            logger.error("Password must be at least {} characters long", MIN_PASSWORD_LENGTH);
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
    }
}
