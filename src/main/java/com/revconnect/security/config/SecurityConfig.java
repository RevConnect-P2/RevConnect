package com.revconnect.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                // disable csrf
                .csrf(csrf -> csrf.disable())


                // ✅ allow everything
                .authorizeHttpRequests(auth -> auth

                        .anyRequest().permitAll()

                )


                // disable default login
                .formLogin(form -> form.disable())


                // disable logout
                .logout(logout -> logout.disable());



        return http.build();

    }


}