package com.cbcode.dealertasks.Users.security.DTOs.Response;

public record JwtAuthResponse(String accessToken, String refreshToken) {

    public JwtAuthResponse {
        validateAccessToken(accessToken);
        validateRefreshToken(refreshToken);
    }

    /**
     * Validates the access token.
     * @param accessToken - The access token to be validated.
     * @throws IllegalArgumentException - If the access token is null or empty.
     */
    private void validateAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
    }

    /**
     * Validates the refresh token.
     * @param refreshToken - The refresh token to be validated.
     * @throws IllegalArgumentException - If the refresh token is null or empty.
     */
    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }
    }
}
