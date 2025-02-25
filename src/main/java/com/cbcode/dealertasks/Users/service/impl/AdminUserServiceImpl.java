package com.cbcode.dealertasks.Users.service.impl;

import com.cbcode.dealertasks.ExceptionsConfig.*;
import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.model.Enums.EnumRole;
import com.cbcode.dealertasks.Users.model.Role;
import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Users.repository.RoleRepository;
import com.cbcode.dealertasks.Users.repository.UserRepository;
import com.cbcode.dealertasks.Users.service.AdminUserService;
import com.cbcode.dealertasks.Users.service.impl.DTOsResponses.UserDeletionResponse;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);
    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AdminUserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /**
     * Delete user by ID, with validation to prevent deletion of admin users by non-admin users.
     * @param id - The ID of the user to be deleted.
     * @return - UserDeletionResponse object containing the ID, email, message, and timestamp of the deletion.
     * @throws IllegalArgumentException - If the ID provided is null or less than or equal to 0.
     * @throws UserNotFoundException - If the user with the provided ID is not found in the database.
     * @throws UserDeletionException - If a database error occurs during the deletion process.
     * @throws OperationNotPermittedException - If non-admin users attempt deletion of admin users.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDeletionResponse deleteUser(Long id) {
        // Input validation
        validateId(id);
        logger.info("Attempting to delete user with id: {}", id);
        try {
            // Fetch user by ID and throw exception if user not found
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("User deletion failed: User not found with ID: {}", id);
                        return new UserNotFoundException(String.format("%s: %d", USER_NOT_FOUND, id));
                    });
            // Validate user deletion permissions and block deletion of admin users by non-admin users
            validateUserDeletion(user);

            // Perform user deletion
            userRepository.delete(user);
            logger.info("User deleted successfully with ID: {} and email: {}", id, user.getEmail());

            // Return a user deletion response object with ID, email, message, and timestamp
            return new UserDeletionResponse(
                    id,
                    user.getEmail(),
                    "User deleted successfully",
                    LocalDateTime.now()
            );
        } catch (DataAccessException e) {
            logger.error("Database error while deleting user with ID: {}", id, e);
            throw new UserDeletionException("Failed to delete user due to database error", e);
        }
    }

    /**
     * Update the Role of a User by ID and Role ID.
     * @param id - The ID of the user to be updated.
     * @param roleNames - Set of role names to be updated.
     * @return - Updated UserDto object containing the updated user details.
     * @throws IllegalArgumentException - If the ID provided is null or less than or equal to 0, or if the roleNames set is null or empty.
     * @throws RoleNotFoundException - If the role provided in the UserDto is not found in the database.
     * @throws UserUpdateException - If a database error occurs during the update process.
     * @throws OperationNotPermittedException - If non-admin users attempt unauthorized updates.
     * @see AdminUserService#adminUpdateUser(Long, UserDto) for updating user details.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDto updateUserRole(Long id, Set<String> roleNames) {
        validateRoleInput(id, roleNames);
        logger.info("Admin updating roles for user with ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyAdminAccess(auth);

        try {
            User user = findUserById(id);
            logger.debug("Found user: {} for role update", user.getEmail());

            Set<Role> roleEntities = fetchOrCreateRoleEntities(roleNames);
            updateUserRole(user, roleEntities);

            User updatedUser = userRepository.save(user);
            logger.info("User roles updated successfully: {}", updatedUser.getEmail());

            return convertToDto(updatedUser);
        } catch (DataAccessException e) {
            logger.error("Database error while updating roles for user with ID: {}", id, e);
            throw new UserUpdateException("Failed to update user roles due to database error.", e);
        }
    }

    /**
     * Fetch all users from the database with pagination.
     * @param pageable - Pageable object containing page number, page size, and sorting details.
     * @return - Page object containing a list of UserDto objects.
     * @throws UserRetrievalException - If a database error occurs during the user retrieval process.
     */
    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        validatePageable(pageable);
        logger.info("Fetching all users with page request: {}", pageable);

        try {
            Page<User> userPage = userRepository.findAll(pageable);
            logger.debug("Retrieved {} users on page {} of size {}", userPage.getNumberOfElements(), userPage.getNumber(), userPage.getTotalPages());

            if (userPage.isEmpty()) {
                logger.error("No users found for page request: {}", pageable);
                return Page.empty(pageable);
            }
            return userPage.map(this::convertToDto);
        } catch (DataAccessException e) {
            logger.error("Database error while fetching users with pageable: {}", pageable, e);
            throw new UserRetrievalException("Failed to retrieve users due to database error", e);
        }
    }

    /**
     * Get user by ID with validation to prevent null or invalid IDs.
     * @param id - The ID of the user to be fetched.
     * @return - UserDto object containing the user details.
     * @throws UserRetrievalException - If a database error occurs during the user retrieval process.
     */
    @Override
    public UserDto getUserById(Long id) {
        validateId(id);
        logger.info("Attempting to fetch user with ID: {}", id);

        try {
            User user= findUserById(id);
            logger.debug("Successfully retrieved user: {} for ID: {}", user.getEmail(), id);

            return convertToDto(user);
        } catch (DataAccessException e) {
            logger.error("Database error while fetching user with ID: {}", id, e);
            throw new UserRetrievalException("Failed to retrieve user due to database error", e);
        }
    }

    /**
     * Update user details by an admin user, with validation to prevent unauthorized updates.
     * @param id - The ID of the user to be updated.
     * @param userDto - New user details to be updated.
     * @return - Updated UserDto object containing the updated user details.
     * @throws IllegalArgumentException - If the ID provided is null or less than or equal to 0.
     * @throws UserNotFoundException - If the user with the provided ID is not found in the database.
     * @throws OperationNotPermittedException - If non-admin users attempt unauthorized updates.
     * @throws RoleNotFoundException - If the role provided in the UserDto is not found in the database.
     * @see AdminUserService#updateUserRole(Long, Set<Role>) for updating user roles.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDto adminUpdateUser(Long id, @NotNull UserDto userDto) {
        validateInput(id, userDto);
        logger.info(("Admin initiating update for user ID: {}"), id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyAdminAccess(auth);

        try {
            User user = findUserById(id);
            logger.debug("Found user: {} for admin update", user.getEmail());

            validateUpdateRestrictions(user, userDto);
            updateUserFields(user, userDto);

            User updatedUser = userRepository.save(user);
            logger.info("User updated successfully: {}", updatedUser.getEmail());

            return convertToDto(updatedUser);
        } catch (DataAccessException e) {
            logger.error("Database error while updating user with ID: {}", id, e);
            throw new UserUpdateException("Failed to update user due to database error", e);
        }
    }

    /**
     * Disable a user by ID.
     * @param id - The ID of the user to be disabled.
     * @throws IllegalArgumentException - If the ID provided is null or less than or equal to 0.
     * @throws UserNotFoundException - If the user with the provided ID is not found in the database.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDto disableUser(Long id) {
        validateId(id);
        logger.info("Disabling user with ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyAdminAccess(auth);

        try {
            User user = findUserById(id);
            logger.debug("Found user: {} for disable operation", user.getEmail());

            validateDisableOperations(user, auth);
            user.setEnabled(false);
            user.setUpdatedAt(LocalDateTime.now()); // Track when it was disabled
            user.setLastModifiedBy(auth.getName());

            User updatedUser = userRepository.save(user);
            logger.info("User {} disabled successfully by: {}", updatedUser.getEmail(), auth.getName());
            return convertToDto(updatedUser);
        } catch (DataAccessException e) {
            logger.error("Database error while disabling user with ID: {}", id, e);
            throw new UserUpdateException("Failed to disable user due to database error", e);
        }
    }

    /**
     * Enable a user by ID.
     * @param id - The ID of the user to be enabled.
     * @throws IllegalArgumentException - If the ID provided is null or less than or equal to 0.
     * @throws UserNotFoundException - If the user with the provided ID is not found in the database.
     */
    @Override
    public UserDto enableUser(Long id) {
        validateId(id);
        logger.info("Initiating enable request for user with ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        verifyAdminAccess(auth);

        try {
            User user = findUserById(id);
            logger.debug("Found user: {} for enable operation", user.getEmail());

            validateEnableOperation(user);
            user.setEnabled(true);
            user.setUpdatedAt(LocalDateTime.now()); // Track when it was enabled
            user.setLastModifiedBy(auth.getName());

            User updatedUser = userRepository.save(user);
            logger.info("User {} enabled successfully by: {}", updatedUser.getEmail(), auth.getName());
            return convertToDto(user);
        } catch (DataAccessException e) {
            logger.error("Database error while enabling user with ID: {}", id, e);
            throw new UserUpdateException("Failed to enable user due to database error", e);
        }
    }

    private void validateUserDeletion(User user) {
        if (isAdmin(user)) {
            logger.warn("Blocked attempt to delete admin user: {}", user.getEmail());
            throw new OperationNotPermittedException("Deletion of admin users is not permitted: " + user.getEmail());
        }
    }

    private @NotNull Boolean isAdmin(@NotNull User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return user.getEmail().equals(currentPrincipalName) && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            logger.error("Pageable parameter cannot be null");
            throw new IllegalArgumentException("Pageable parameter is required");
        }
        if (pageable.getPageSize() <= 0 || pageable.getPageNumber() < 0) {
            logger.error("Invalid pageable parameters: size={}, page={}", pageable.getPageSize(), pageable.getPageNumber());
            throw new IllegalArgumentException("Page size must be positive and page number cannot be negative");
        }
    }

    private UserDto convertToDto(User user) {
        try {
            return modelMapper.map(user, UserDto.class);
        } catch (MappingException e) {
            logger.error("Error mapping user {} to DTO", user.getId(), e);
            throw new UserMappingException("Failed to map user to DTO", e);
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found for update with ID: {}", id);
                    return new UserNotFoundException(USER_NOT_FOUND + ": " + id);
                });
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid user with ID: {}", id);
            throw new IllegalArgumentException("User ID must be a positive number");
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

    private @NotNull Boolean shouldUpdatePassword(@NotNull UserDto userDto) {
        return userDto.getPassword() != null && !userDto.getPassword().isEmpty();
    }

    private void validateAndUpdatePassword(@NotNull User user, @NotNull UserDto userDto) {
        if (userDto.getPassword().length() < 8) {
            logger.error("Password must be at least 8 characters long");
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    private void validateInput(Long id, UserDto userDto) {
        if (id == null || id <= 0) {
            logger.error("Invalid user provided with ID: {}", id);
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (userDto == null) {
            logger.error("User details cannot be null for update");
            throw new IllegalArgumentException("User details are required for update");
        }
    }

    private void verifyAdminAccess(Authentication auth) {
        if (!hasAdminRole(auth)) {
            logger.warn("Non-admin user {} attempted admin update", auth.getName());
            throw new OperationNotPermittedException("Admin privileges required for this operation");
        }
    }

    private void validateUpdateRestrictions(@NotNull User user, @NotNull UserDto userDto) {
        if (!userDto.getEmail().equals(user.getEmail())) {
            logger.warn("Attempt to change email from {} to {}", user.getEmail(), userDto.getEmail());
            throw new OperationNotPermittedException("Email updates are not permitted");
        }
        if (!Objects.deepEquals(userDto.getRoles(), user.getRoles())) {
            logger.warn("Attempt to change roles for user: {}", user.getEmail());
            throw new OperationNotPermittedException("Role updates must use the dedicated endpoint");
        }
    }

    private @NotNull Boolean hasAdminRole(@NotNull Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private @NotNull Boolean hasRole(@NotNull User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
    }

    private void validateRoleInput(Long id, Set<String> roleNames) {
        if (id == null || id <= 0) {
            logger.error("Invalid given user ID: {}", id);
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (roleNames == null || roleNames.isEmpty()) {
            logger.error("No roles provided for user ID: {}", id);
            throw new IllegalArgumentException("At least one role must be provided");
        }
    }

    private Set<Role> fetchOrCreateRoleEntities(@NotNull Set<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    try {
                        EnumRole enumRole = EnumRole.valueOf(roleName);
                        return roleRepository.findByName(enumRole)
                                .orElseGet(() -> {
                                    logger.info("Creating new role: {}", roleName);
                                    Role newRole = new Role();
                                    newRole.setName(enumRole);
                                    return roleRepository.save(newRole);
                                });
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid role name provided: {}", roleName);
                        throw new RoleNotFoundException("Role not found: " + roleName);
                    }
                })
                .collect(Collectors.toSet());
    }


    private void updateUserRole(@NotNull User user, Set<Role> roles) {
        user.getRoles().clear();
        user.getRoles().addAll(roles);
    }

    private void validateDisableOperations(@NotNull User user, @NotNull Authentication auth) {
        User currentUser = findUserByEmail(auth.getName());
        if (currentUser.getId().equals(user.getId())) {
            logger.warn("Admin {} attempted to disable themselves", auth.getName());
            throw new OperationNotPermittedException("Disabling own account is not permitted");
        }
        if (hasRole(user)) {
            logger.warn("Non-admin {} attempted to disable admin user: {}", auth.getName(), user.getEmail());
            throw new OperationNotPermittedException("Disabling admin users is not permitted");
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
    }

    private void validateEnableOperation(@NotNull User user) {
        if (user.isEnabled()) {
            logger.warn("User {} is already enabled", user.getEmail());
            throw new OperationNotPermittedException("User is already enabled");
        }
    }
}
