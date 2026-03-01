package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Long getUserIdByUsername(String loginValue) {

        User user = userRepository.findByUsername(loginValue)
                .orElseGet(() ->
                        userRepository.findByEmail(loginValue)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "User not found with login: " + loginValue
                                        )
                                )
                );

        return user.getUserId();
    }
}