package com.revconnect.service.impl;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.AuthService;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.repository.UserProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AuthServiceImpl implements AuthService {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileRepository userProfileRepository;


    // ============================
    // REGISTER USER
    // ============================

    @Override
    public User register(RegisterRequest request)
    {

        logger.info("Register request received for email {}", request.getEmail());

        // check email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent())
        {
            logger.error("Registration failed - Email already registered: {}", request.getEmail());
            throw new RuntimeException("Email is already registered");
        }


        // check username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent())
        {
            logger.error("Registration failed - Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username is already Exist");
        }


        logger.info("Creating new user with username {}", request.getUsername());

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType())
                .securityQuestion(request.getSecurityQuestion())
                .securityAnswer(request.getSecurityAnswer())
                .build();

        User savedUser = userRepository.save(user);

        logger.info("User saved successfully with ID {}", savedUser.getUserId());


        /* ===========================
           CREATE DEFAULT PROFILE
           =========================== */

        logger.info("Creating default profile for user {}", savedUser.getUserId());

        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .fullName(savedUser.getUsername())
                .profileVisibility("PUBLIC")
                .profileType(ProfileType.valueOf(savedUser.getUserType()))
                .build();

        userProfileRepository.save(profile);

        logger.info("Default profile created for user {}", savedUser.getUserId());

        return savedUser;
    }




    // ============================
    // LOGIN USER
    // ============================

    @Override
    public User login(LoginRequest request)
    {

        logger.info("Login attempt for email {}", request.getEmail());

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> {

                    logger.error("Login failed - Email not registered: {}", request.getEmail());

                    return new RuntimeException("Email is not registered");
                });



        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
        {

            logger.error("Login failed - Incorrect password for email {}", request.getEmail());

            throw new RuntimeException("Incorrect password");

        }

        logger.info("Login successful for user {}", user.getUserId());

        return user;

    }



    // ============================
    // FIND USER BY EMAIL
    // ============================

    @Override
    public User findByEmail(String email)
    {

        logger.info("Finding user by email {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.error("User not found for email {}", email);

                    return new RuntimeException("No account found with this email");
                });

    }



    // ============================
    // RESET PASSWORD
    // ============================

    @Override
    public void resetPassword(String email,
                              String answer,
                              String newPassword)
    {

        logger.info("Password reset attempt for email {}", email);

        User user = findByEmail(email);


        if (!user.getSecurityAnswer()
                .trim()
                .equalsIgnoreCase(answer.trim()))
        {

            logger.error("Security answer incorrect for email {}", email);

            throw new RuntimeException("Security answer is incorrect");

        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        logger.info("Password reset successful for user {}", user.getUserId());

    }

}