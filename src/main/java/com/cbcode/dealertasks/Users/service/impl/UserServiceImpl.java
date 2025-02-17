package com.cbcode.dealertasks.Users.service.impl;

import com.cbcode.dealertasks.ExceptionsConfig.EmailNotMatchException;
import com.cbcode.dealertasks.ExceptionsConfig.OperationNotPermittedException;
import com.cbcode.dealertasks.ExceptionsConfig.PasswordTooShortException;
import com.cbcode.dealertasks.ExceptionsConfig.UserNotFoundException;
import com.cbcode.dealertasks.ExceptionsConfig.RoleNotFoundException;
import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.model.Role;
import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Users.repository.RoleRepository;
import com.cbcode.dealertasks.Users.repository.UserRepository;
import com.cbcode.dealertasks.Users.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        logger.info("Attempting to update user with id: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        logger.debug("Current authenticated user: {}", currentPrincipalName);

        User user = findUserById(id);

        logger.debug("User found: {}", user.getEmail());

        validateUserUpdatePermissions(authentication, currentPrincipalName, user, userDto);
        validateAndUpdatePassword(user, userDto);

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully");

        return modelMapper.map(updatedUser, UserDto.class);
    }

    private void validateUserUpdatePermissions(Authentication authentication, String currentPrincipalName, User user, UserDto userDto) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!userDto.getEmail().equals(user.getEmail())) {
            logger.warn("Attempt to update another user's email by user: {}", currentPrincipalName);
            throw new EmailNotMatchException("You cannot update another user's email");
        }
        if (!isAdmin && !currentPrincipalName.equals(user.getEmail())) {
            logger.warn("Unauthorized attempt to update user details by: {}", currentPrincipalName);
            throw new OperationNotPermittedException("You are not allowed to update user details");
        }
    }

    private void validateAndUpdatePassword(User user, UserDto userDto) {
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            if (userDto.getPassword().length() < 8) {
                logger.warn("Password update failed: Password too short for user: {}", user.getEmail());
                throw new PasswordTooShortException("Password must be at least 8 characters long");
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            logger.debug("Password updated successfully for user: {}", user.getEmail());
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", id);
                    return new UserNotFoundException(USER_NOT_FOUND);
                });
    }

    @Override
    @Transactional
    public String deleteUser(Long id) {
        logger.info("Attempting to delete user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", id);
                    return new UserNotFoundException(String.format("%s: %d", USER_NOT_FOUND, id));
                });
        if (isAdmin(user)) {
            logger.warn("Attempt to delete admin user: {}", user.getEmail());
            throw new OperationNotPermittedException("You cannot delete an admin user");
        }
        userRepository.delete(user);
        logger.info("User deleted successfully with id: {}", id);
        return "User deleted successfully";
    }

    private Boolean isAdmin(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return user.getEmail().equals(currentPrincipalName) && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        if (userPage.isEmpty()) {
            logger.error("No users found");
            throw new UserNotFoundException("No users found");
        }
        return userPage.map(user -> modelMapper.map(user, UserDto.class));
    }

    @Override
    public UserDto getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        User user= findUserById(id);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto adminUpdateUser(Long id, UserDto userDto) {
        logger.info(("Admin updating user with id: {}"), id);
        User user = findUserById(id);
        if (!userDto.getEmail().equals(user.getEmail()) || !Objects.deepEquals(userDto.getRoles(), user.getRoles())) {
            logger.error("Attempt to update user email or roles for user with ID: {}", id);
            throw new OperationNotPermittedException("You cannot update user email or roles");
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(userDto.isEnabled());

        Set<Role> roles = new LinkedHashSet<>();
        userDto.getRoles().forEach(role -> {
            Role newRole = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + role));
            roles.add(newRole);
        });
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", savedUser.getEmail());
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        logger.info("Disabling user with ID: {}", id);
        User user = findUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
        logger.info("User disabled successfully: {}", user.getEmail());
    }

    @Override
    public void enableUser(Long id) {
        logger.info("Enabling user with ID: {}", id);
        User user = findUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
        logger.info("User enabled successfully: {}", user.getEmail());
    }
}
