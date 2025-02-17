package com.cbcode.dealertasks.Users.controller;

import com.cbcode.dealertasks.Users.security.AuthService;
import com.cbcode.dealertasks.Users.security.DTOs.Request.RefreshTokenRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Request.SignInRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Request.SignUpRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Response.JwtAuthResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // This annotation needs
// to be altered to the specific URL of the frontend application that will consume the API in production
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user.
     * @param signUpRequest the request body containing the user's email, password and full name.
     * @return a ResponseEntity with a message and the URI of the created user.
     */
    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignUpRequest signUpRequest) {
        logger.info("Registering a new user with email: {}", signUpRequest.email());
        authService.registerUser(signUpRequest);
        String message = "User registered successfully!";
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(message);
    }

    /**
     * Logs in a user.
     * @param signInRequest the request body containing the user's email and password.
     * @return a ResponseEntity with the JWT token and the user's full name.
     */
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<JwtAuthResponse> loginUser(@RequestBody @Valid SignInRequest signInRequest) {
        logger.info("Logging in user with email: {}", signInRequest.email());
        return ResponseEntity.ok(authService.loginUser(signInRequest));
    }

    /**
     * Refreshes the JWT token.
     * @param refreshToken the request body containing the refresh token.
     * @return a ResponseEntity with the new JWT token.
     */
    @PostMapping(value = "/refresh", consumes = "application/json")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        logger.info("Refreshing token for user");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    /**
     * Logs out a user.
     * @return a ResponseEntity with a message.
     */
    @PostMapping(value = "/logout")
    public ResponseEntity<?> logoutUser() {
        logger.info("Logging out user");
        authService.logoutUser();
        return ResponseEntity.ok("User logged out successfully!");
    }

    /**
     * Sends an email to the user with a link to reset the password.
     * @param email the user's email.
     * @return a ResponseEntity with a message.
     */
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam(name = "forgot-password") String email) {
        logger.info("Forgot password for user with email: {}", email);
        authService.forgotPassword(email);
        return ResponseEntity.ok("Password reset email sent successfully!");
    }

    /**
     * Validates the reset token.
     * @param token the reset token.
     * @return a ResponseEntity with a message.
     */
    @GetMapping(value = "/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam(name = "token") String token) {
        logger.info("Validating reset token");
        authService.validateResetToken(token);
        return ResponseEntity.ok("Reset token is valid!");
    }

    /**
     * Resets the user's password.
     * @param token the reset token.
     * @param newPassword the new password.
     * @return a ResponseEntity with a message.
     */
    @PostMapping(value = "/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam(name = "token") String token, @RequestParam(name = "new-password") String newPassword) {
        logger.info("Resetting password for user");
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully!");
    }
}
