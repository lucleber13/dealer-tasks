package com.cbcode.dealertasks.Users.security.impl;

import com.cbcode.dealertasks.Users.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${dealer_management_system.security.jwt.secret}")
    private String jwtSecret;
    @Value("${dealer_management_system.security.jwt.expiration}")
    private long jwtExpirationInMs;
    @Value("${dealer_management_system.security.jwt.refreshExpiration}")
    private long jwtRefreshExpirationInMs;

    /**
     * The validateConfiguration method is used to validate the configuration values for the JWT secret and expiration.
     * The method checks if the JWT secret is configured and if the expiration values are positive.
     * If the JWT secret is not configured or the expiration values are not positive, the method throws an IllegalStateException.
     * @throws IllegalStateException - If the JWT secret is not configured or the expiration values are not positive.
     */
    @PostConstruct
    public void validateConfiguration() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            logger.error("JWT secret is not configured");
            throw new IllegalStateException("JWT secret is not configured");
        }
        if (jwtExpirationInMs <= 0 || jwtRefreshExpirationInMs <= 0) {
            logger.error("JWT expiration must be a positive value");
            throw new IllegalStateException("JWT expiration must be a positive value");
        }
    }

    /**
     * The generateJwtToken method is used to generate a JWT token for the given UserDetails object.
     * The method creates a JWT token with the subject set to the username of the UserDetails object;
     * the issuedAt date set to the current time, the expiration date set to the current time plus the expiration value,
     * and signs the token using the signing key.
     * @param userDetails - The UserDetails object for which to generate the JWT token.
     * @return - The generated JWT token.
     */
    @Override
    public String generateJwtToken(UserDetails userDetails) {
        logger.info("Generating JWT token for user: {}", userDetails.getUsername());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * The getSignInKey method is used to generate the signing key for the JWT token.
     * The method converts the JWT secret to bytes and creates a SecretKey using the HMAC SHA algorithm.
     * The StandardCharsets.UTF_8 encoding is used to convert the JWT secret to bytes.
     * @return - The signing key for the JWT token.
     */
    private SecretKey getSignInKey() {
        byte[]  keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * The getUsernameFromToken method is used to extract the username from the JWT token.
     * The method extracts the subject claim from the token using the extractClaim method.
     * @param token - The JWT token from which to extract the username.
     * @return - The username extracted from the JWT token.
     */
    @Override
    public String getUsernameFromToken(String token) {
        logger.debug("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * The extractClaim method is a generic method used to extract a claim from the JWT token.
     * The method extracts the claim using the claimsResolver function.
     * @param token - The JWT token from which to extract the claim.
     * @param claimsResolver - The function used to extract the claim from the token.
     * @param <T> - The type of the claim to extract.
     * @return - The extracted claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * The extractAllClaims method is used to extract all claims from the JWT token.
     * The method verifies the token using the signing key and extracts the payload from the token.
     * @param token - The JWT token from which to extract the claims.
     * @return - The extracted claims from the JWT token.
     * @throws JwtException - If the token is invalid.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }

    /**
     * The validateToken method is used to validate the JWT token for the given UserDetails object.
     * The method extracts the username from the token and compares it with the username from the UserDetails object.
     * The method also checks if the token is expired using the isTokenExpired method.
     * @param token - The JWT token to validate.
     * @param userDetails - The UserDetails object to validate.
     * @throws JwtException - If the token is invalid.
     * @return - True if the token is valid for the UserDetails object, false otherwise.
     */
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Validating token for user: {}", userDetails.getUsername());
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * The isTokenExpired method is used to check if the JWT token is expired.
     * The method extracts the expiration date from the token using the extractExpiration method.
     * The method returns true if the expiration date is before the current date, false otherwise.
     * @param token - The JWT token to check for expiration.
     * @return - True if the token is expired, false otherwise.
     */
    @Override
    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    /**
     * The extractExpiration method is used to extract the expiration date from the JWT token.
     * The method extracts the expiration claim from the token using the extractClaim method.
     * @param token - The JWT token from which to extract the expiration date.
     * @return - The expiration date extracted from the JWT token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * The generateRefreshToken method is used to generate a refresh token for the given UserDetails object.
     * The method creates a refresh token with the subject set to the username of the UserDetails object;
     * the issuedAt date set to the current time, the expiration date set to the current time plus the refresh expiration value,
     * and signs the token using the signing key.
     * @param claims - The claims to include in the refresh token.
     * @param userDetails - The UserDetails object for which to generate the refresh token.
     * @return - The generated refresh token.
     */
    @Override
    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        logger.info("Generating refresh token for user: {}", userDetails.getUsername());
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
                .signWith(getSignInKey())
                .compact();
    }
}
