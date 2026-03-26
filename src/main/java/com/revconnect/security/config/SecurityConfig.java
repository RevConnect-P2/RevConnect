package com.revconnect.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Configuration
public class SecurityConfig {

    private static final Logger logger =
            LogManager.getLogger(SecurityConfig.class);

    // PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {

        logger.info("BCryptPasswordEncoder bean created");

        return new BCryptPasswordEncoder();
    }

    // SECURITY FILTER CHAIN
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        logger.info("Configuring Spring Security filter chain");

        http
                // Disable CSRF for now (okay for session-based UI)
                .csrf(csrf -> {
                    logger.debug("CSRF protection disabled");
                    csrf.disable();
                })

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/forgot-password",
                                "/reset-password",
                                "/auth/me",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        // Everything else allowed (manual session check)
                        .anyRequest().permitAll()
                )

                // Disable default Spring Security login
                .formLogin(form -> {
                    logger.debug("Spring Security default login disabled");
                    form.disable();
                })

                // Disable default logout
                .logout(logout -> {
                    logger.debug("Spring Security default logout disabled");
                    logout.disable();
                });

        logger.info("Spring Security configuration completed");

        return http.build();
    }
}