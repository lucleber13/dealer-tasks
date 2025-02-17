package com.cbcode.dealertasks.Users.security.DTOs.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record RefreshTokenRequest(String refreshToken) {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenRequest.class);

    public RefreshTokenRequest {
        logger.info("Creating a new refresh token request");
        validateRefreshToken(refreshToken);
    }

    /**
     * Validates the refresh token.
     * @param refreshToken - The refresh token to be validated.
     * @throws IllegalArgumentException - If the refresh token is null or empty.
     */
    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            logger.error("Refresh token cannot be null or empty");
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }
    }
}
