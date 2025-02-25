package com.cbcode.dealertasks.Users.service.impl;

import com.cbcode.dealertasks.ExceptionsConfig.*;
import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Users.repository.UserRepository;
import com.cbcode.dealertasks.Users.service.UserService;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String USER_NOT_FOUND = "User not found with ID";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$");
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /**
     * Update user details, with validation to prevent unauthorized updates and password changes.
     * @param id - The ID of the user to be updated.
     * @param userDto - New user details to be updated.
     * @return - Updated UserDto object containing the updated user details.
     * @throws IllegalArgumentException - If the ID provided is null or less than or equal to 0.
     * @throws UserNotFoundException - If the user with the provided ID is not found in the database.
     * @throws UserUpdateException - If a database error occurs during the update process.
     * @throws EmailNotMatchException - If the email in the UserDto does not match the email in the database.
     * @throws OperationNotPermittedException - If non-admin users attempt unauthorized updates.
     * @throws PasswordTooShortException - If the new password provided is less than the minimum password length.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDto updateUser(Long id, UserDto userDto) {
        // Input validation
        validateInput(id, userDto);
        logger.info("Initiating user update for ID: {}", id);

        // Fetch current user details and validate update permissions
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        // Fetch user by ID.
        User user = findUserById(id);
        logger.debug("Found user: {} for update by: {}", user.getEmail(), currentPrincipalName);

        // Validate user update permissions and update user fields if validation passes successfully
        validateUserUpdatePermissions(authentication, currentPrincipalName, user, userDto);
        updateUserFields(user, userDto);

        try {
            // Save updated user details to the database
            User updatedUser = userRepository.save(user);
            logger.info("Successfully updated user: {}", updatedUser.getEmail());

            // Return updated user details in UserDto format for response output
            return modelMapper.map(updatedUser, UserDto.class);
        } catch (DataAccessException e) {
            logger.error("Database error while updating user with ID: {}", id, e);
            throw new UserUpdateException("Failed to update user due to database error", e);
        }
    }

    private void validateInput(Long id, UserDto userDto) {
        // Validate input parameters for a user update process to prevent null or invalid values from being processed
        if (id == null || id <= 0) {
            logger.error("Invalid user ID: {}", id);
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (userDto == null) {
            logger.error("User DTO cannot be null for ID: {}", id);
            throw new IllegalArgumentException("User DTO cannot be null");
        }
    }

    private void validateUserUpdatePermissions(@NotNull Authentication authentication, String currentPrincipalName,
                                               @NotNull User user, @NotNull UserDto userDto) {
        // Check if the current user is an admin
        boolean isAdmin = hasRole(authentication);

        // Check if the user email in the UserDto matches the email in the database
        if (!userDto.getEmail().equals(user.getEmail())) {
            logger.warn("User {} attempted to change email from {} to {}", currentPrincipalName, user.getEmail(), userDto.getEmail());
            throw new EmailNotMatchException("You cannot update another user's email");
        }
        // Check if the current user is an admin or the user is updating their own details
        if (!isAdmin && !currentPrincipalName.equals(user.getEmail())) {
            logger.warn("Unauthorized update attempt by {} on user {}", currentPrincipalName, user.getEmail());
            throw new OperationNotPermittedException("You are not allowed to update this user details");
        }
    }

    private void updateUserFields(@NotNull User user, @NotNull UserDto userDto) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        // Check if the user is updating their own password and validate the new password
        if (shouldUpdatePassword(userDto)) {
            validateAndUpdatePassword(user, userDto);
        }
        // Set updated timestamp for the user details update process
        user.setUpdatedAt(LocalDateTime.now());
    }

    private boolean shouldUpdatePassword(@NotNull UserDto userDto) {
        return userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty();
    }

    private void validateAndUpdatePassword(@NotNull User user, @NotNull UserDto userDto) {
        String newPassword = userDto.getPassword();
        // Check if the new password meets the minimum length requirement and throw an exception if it does not
       if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            logger.warn("Password update rejected - too short for user: {}", user.getEmail());
            throw new PasswordTooShortException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
       }
         // Check if the new password meets the required format and throw an exception if it does not
       if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
           logger.warn("Password update rejected - invalid format for user: {}", user.getEmail());
           throw new PasswordTooShortException("Password must contain at least " + MIN_PASSWORD_LENGTH +
                   " characters, including one number and one special character (!@#$%^&*)");
       }
       // Check if the new password is the same as the current password and skip the update process
       if (passwordEncoder.matches(newPassword, user.getPassword())) {
           logger.debug("Password unchanged for user: {}", user.getEmail());
           return;
       }
         // Encode the new password and update the user password field
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found for update with ID: {}", id);
                    return new UserNotFoundException(USER_NOT_FOUND + ": " + id);
                });
    }

    private boolean hasRole(@NotNull Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
