package com.cbcode.dealertasks.Users.security;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.security.DTOs.Request.RefreshTokenRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Request.SignInRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Request.SignUpRequest;
import com.cbcode.dealertasks.Users.security.DTOs.Response.JwtAuthResponse;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    JwtAuthResponse loginUser(SignInRequest signInRequest);

    JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logoutUser();

    UserDto forgotPassword(String email);

    UserDto validateResetToken(String token);

    void resetPassword(String token, String newPassword);
}
