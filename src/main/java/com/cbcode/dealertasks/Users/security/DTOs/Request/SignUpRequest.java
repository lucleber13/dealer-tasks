package com.cbcode.dealertasks.Users.security.DTOs.Request;

import com.cbcode.dealertasks.Users.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

public record SignUpRequest(String firstName, String lastName, String email, String password, Set<Role> roles) {

    private static final Logger logger = LoggerFactory.getLogger(SignUpRequest.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    public SignUpRequest {
        validateFirstName(firstName);
        validateLastName(lastName);
        validateEmail(email);
        validatePassword(password);
        validateRoles(roles);
    }

    /**
     * Validates the first name of the user.
     * @param firstName - The first name of the user.
     * @throws IllegalArgumentException if the first name is null or empty.
     */
    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            logger.error("First name cannot be null or empty");
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
    }

    /**
     * Validates the last name of the user.
     * @param lastName - The last name of the user.
     * @throws IllegalArgumentException if the last name is null or empty.
     */
    private void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            logger.error("Last name cannot be null or empty");
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
    }

    /**
     * Validates the email of the user.
     * @param email - The email of the user.
     * @throws IllegalArgumentException if the email is null or empty, or if the email is not a valid email format.
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
     * @param password - The password of the user.
     * @throws IllegalArgumentException if the password is null or empty, or if the password is less than eight characters long.
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

    /**
     * Validates the roles of the user.
     * @param roles - The roles of the user.
     * @throws IllegalArgumentException if the roles are null or empty, or if the roles are not unique.
     */
    private void validateRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            logger.error("Roles cannot be null or empty");
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }
        // Ensure roles are unique and valid
        if (roles.size() != roles.stream().distinct().count()) {
            logger.error("Duplicate roles found");
            throw new IllegalArgumentException("Duplicate roles found");
        }
    }

    /**
     * Returns an unmodifiable set of roles.
     * @return an unmodifiable set of roles.
     */
    public Set<Role> roles() {
        return Collections.unmodifiableSet(roles);
    }
}
