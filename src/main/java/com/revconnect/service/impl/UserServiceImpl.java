package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // existing login logic
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

    // 🔍 SEARCH USERNAMES (for navbar search suggestions)
    @Override
    public List<String> searchUsernames(String keyword) {

        return userRepository.findByUsernameContainingIgnoreCase(keyword)
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}