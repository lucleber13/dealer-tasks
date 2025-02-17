package com.cbcode.dealertasks.Users.security.impl;

import com.cbcode.dealertasks.Users.repository.UserRepository;
import com.cbcode.dealertasks.Users.security.SecurityUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserServiceImpl implements SecurityUserService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUserServiceImpl.class);
    private final UserRepository userRepository;

    public SecurityUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return
     */
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                logger.info("Loading user by email: " + email);
                return userRepository.findByEmail(email)
                        .map(AuthUser::new)
                        .orElseThrow(() -> {
                            logger.error("User not found with email: {}", email);
                            return new UsernameNotFoundException("User not found with email: " + email);
                        });
            }
        };
    }
}
