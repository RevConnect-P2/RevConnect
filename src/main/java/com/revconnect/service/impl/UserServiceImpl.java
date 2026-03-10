package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserServiceImpl implements UserService {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // existing login logic
    @Override
    public Long getUserIdByUsername(String loginValue) {

        logger.info("Fetching user ID for login value: {}", loginValue);

        User user = userRepository.findByUsername(loginValue)
                .orElseGet(() -> {

                    logger.info("Username not found, trying email lookup for {}", loginValue);

                    return userRepository.findByEmail(loginValue)
                            .orElseThrow(() -> {

                                logger.error("User not found with login value: {}", loginValue);

                                return new RuntimeException(
                                        "User not found with login: " + loginValue
                                );
                            });
                });

        logger.info("User found with ID {}", user.getUserId());

        return user.getUserId();
    }

    // 🔍 SEARCH USERNAMES (for navbar search suggestions)
    @Override
    public List<String> searchUsernames(String keyword) {

        logger.info("Searching usernames with keyword: {}", keyword);

        List<String> usernames = userRepository.findByUsernameContainingIgnoreCase(keyword)
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        logger.info("Found {} usernames for keyword {}", usernames.size(), keyword);

        return usernames;
    }

    @Override
    public String getUsernameByUserId(Long userId) {

        logger.info("Fetching username for user ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {

                    logger.error("User not found with ID {}", userId);

                    return new RuntimeException("User not found with id: " + userId);
                });

        logger.info("Username found for user ID {} is {}", userId, user.getUsername());

        return user.getUsername();
    }
}