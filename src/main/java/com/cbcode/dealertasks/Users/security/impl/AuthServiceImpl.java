package com.cbcode.dealertasks.Users.security.impl;

import com.cbcode.dealertasks.ExceptionsConfig.*;
import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.model.Role;
import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Users.repository.RoleRepository;
import com.cbcode.dealertasks.Users.repository.UserRepository;
import com.cbcode.dealertasks.Users.security.AuthService;
import com.cbcode.dealertasks.Users.security.DTOs.Request.RefreshTokenRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Request.SignInRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Request.SignUpRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Response.JwtAuthResponse;
import com.cbcode.dealertasks.Users.security.JwtService;
import com.cbcode.dealertasks.Users.service.EmailService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                           EmailService emailService, AuthenticationManager authenticationManager, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    /**
     * Register a new user.
     * Validates the user details and password.
     * If the email is already in use, an exception is thrown.
     * If the password is too short, an exception is thrown.
     * If the roles are not found, an exception is thrown.
     * If the user is successfully registered, a success message is logged.
     * @param signUpRequest - The user details to register. Contains the user's first name, last name, email, password and roles.
     * @throws UserAlreadyExistsException - If the email is already in use.
     * @throws PasswordTooShortException - If the password is too short.
     */
    @Override
    public void registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            logger.error("Email already in use: {}", signUpRequest.email());
            throw new UserAlreadyExistsException("User with email " + signUpRequest.email() + " already exists");
        }
        if (signUpRequest.password().length() < 8) {
            logger.error("Password must be at least 8 characters long");
            throw new PasswordTooShortException("Password must be at least 8 characters long");
        }
        Set<Role> roles = signUpRequest.roles()
                .stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RoleNotFoundException("Role not found: " + role)))
                .collect(Collectors.toSet());

        User user = new User();
        user.setFirstName(signUpRequest.firstName());
        user.setLastName(signUpRequest.lastName());
        user.setEmail(signUpRequest.email());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setRoles(roles);
        user.setEnabled(true);
        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getEmail());
    }

    /**
     * Login a user.
     * Authenticates the user with the given email and password.
     * If the user is successfully authenticated, a JWT token is generated and returned.
     * If the user is not found, an exception is thrown.
     * If the password is incorrect, an exception is thrown.
     * If the user is successfully logged in, a success message is logged.
     * @param signInRequest - The user details to login. Contains the user's email and password.
     * @return - The JWT token.
     */
    @Override
    public JwtAuthResponse loginUser(SignInRequest signInRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        signInRequest.email(),
                        signInRequest.password()));
        if (!authentication.isAuthenticated()) {
            logger.error("Invalid credentials");
            throw new InvalidCredentialsException("Invalid credentials");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateJwtToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userDetails);

        logger.info("User logged in successfully: {}", userDetails.getUsername());
        return new JwtAuthResponse(jwt, refreshToken);
    }

    /**
     * Refresh a JWT token.
     * Validates the refresh token and generates a new JWT token.
     * If the refresh token is invalid, an exception is thrown.
     * If the token is successfully refreshed, a success message is logged.
     * @param refreshTokenRequest - The refresh token.
     * @return - The new JWT token.
     */
    @Override
    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtService.getUsernameFromToken(refreshTokenRequest.refreshToken());
        UserDetails userDetails = userRepository.findByEmail(username)
                .map(AuthUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
        if (!jwtService.validateToken(refreshTokenRequest.refreshToken(), userDetails)) {
            logger.error("Invalid refresh token");
            throw new InvalidTokenException("Invalid refresh token");
        }

        String jwt = jwtService.generateJwtToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userDetails);

        logger.info("Token refreshed successfully for user: {}", username);
        return new JwtAuthResponse(jwt, refreshToken);
    }

    /**
     * Logout a user.
     * Clears the security context.
     * If the user is successfully logged out, a success message is logged.
     */
    @Override
    public void logoutUser() {
        SecurityContextHolder.clearContext();
        logger.info("User logged out successfully");
    }

    /**
     * Forgot password.
     * Generates a reset token and sends an email to the user with a link to reset the password.
     * If the user is not found, an exception is thrown.
     * If the reset token is successfully generated and the email is sent, a success message is logged.
     * @param email - The user's email.
     * @return - The user details.
     * @throws UserNotFoundException - If the user is not found.
     */
    @Override
    public UserDto forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User with email {} not found", email);
                    return new UserNotFoundException("User with email " + email + " not found");
                });

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiration(LocalDateTime.now().plusMinutes(30)); // The Token expires in 1 hour
        userRepository.save(user);

        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken; // Frontend reset password link with token as query parameter
        emailService.sendPasswordResetEmail(email, resetLink);

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Validate reset token.
     * Validates the reset token.
     * If the token is invalid, an exception is thrown.
     * If the token is expired, an exception is thrown.
     * @param token - The reset token.
     * @return - The user details.
     * @throws InvalidTokenException - If the token is invalid.
     * @throws TokenExpiredException - If the token is expired.
     */
    @Override
    public UserDto validateResetToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid reset token");
                    return new InvalidTokenException("Invalid reset token");
                });

        if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            logger.error("Reset token expired");
            throw new TokenExpiredException("Reset token expired");
        }

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Reset password.
     * Resets the user's password.
     * If the token is invalid, an exception is thrown.
     * If the token is expired, an exception is thrown.
     * If the password is successfully reset, a success message is logged.
     * @param token - The reset token.
     * @param newPassword - The new password.
     * @throws InvalidTokenException - If the token is invalid.
     * @throws TokenExpiredException - If the token is expired.
     */
    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid reset token");
                    return new InvalidTokenException("Invalid reset token");
                });

        if (user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            logger.error("Reset token expired");
            throw new TokenExpiredException("Reset token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }
}
