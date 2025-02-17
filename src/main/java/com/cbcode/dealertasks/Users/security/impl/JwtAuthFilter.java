package com.cbcode.dealertasks.Users.security.impl;

import com.cbcode.dealertasks.Users.security.JwtService;
import com.cbcode.dealertasks.Users.security.SecurityUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final SecurityUserService userSecurityService;

    public JwtAuthFilter(JwtService jwtService, SecurityUserService userSecurityService) {
        this.jwtService = jwtService;
        this.userSecurityService = userSecurityService;
    }

    /**
     * Method to intercept requests and validate JWT tokens.
     * If a valid token is found, the user is authenticated and added to the security context.
     * If the token is invalid, the security context is cleared.
     * If no token is found, the request is passed to the next filter in the chain.
     * @param request - The request object.
     * @param response - The response object.
     * @param filterChain - The filter chain object.
     * @throws ServletException - If an error occurs during the filter process.
     * @throws IOException - If an error occurs during the filter process.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authorizationHeader.substring(7);

        try {
            final String userEmail = jwtService.getUsernameFromToken(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userSecurityService.userDetailsService().loadUserByUsername(userEmail);
                if (jwtService.validateToken(jwt, userDetails)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(securityContext);
                    logger.info("Authenticated user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to authenticate user: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
