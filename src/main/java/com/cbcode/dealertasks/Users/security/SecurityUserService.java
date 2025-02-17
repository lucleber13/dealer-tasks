package com.cbcode.dealertasks.Users.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface SecurityUserService {
    UserDetailsService userDetailsService();
}
