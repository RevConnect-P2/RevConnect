package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;   // ✅ THIS FIXES "Cannot resolve symbol"

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
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