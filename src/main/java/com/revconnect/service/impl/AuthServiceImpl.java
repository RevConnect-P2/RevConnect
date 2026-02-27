package com.revconnect.service.impl;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {



    @Autowired
    private UserRepository userRepository;



    @Autowired
    private PasswordEncoder passwordEncoder;



    // ============================
    // REGISTER USER
    // ============================

    @Override
    public User register(RegisterRequest request)
    {

        // check email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent())
        {
            throw new RuntimeException("Email is already registered");
        }


        // check username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent())
        {
            throw new RuntimeException("Username is already Exist");
        }



        User user = User.builder()

                .email(request.getEmail())

                .username(request.getUsername())

                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                .userType(
                        request.getUserType()
                )

                .securityQuestion(
                        request.getSecurityQuestion()
                )

                .securityAnswer(
                        request.getSecurityAnswer()
                )

                .build();



        return userRepository.save(user);

    }




    // ============================
    // LOGIN USER
    // ============================

    @Override
    public User login(LoginRequest request)
    {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Email is not registered")
                );



        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
        {

            throw new RuntimeException("Incorrect password");

        }



        return user;

    }



    // ============================
    // FIND USER BY EMAIL
    // ============================

    @Override
    public User findByEmail(String email)
    {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("No account found with this email")
                );

    }



    // ============================
    // RESET PASSWORD
    // ============================

    @Override
    public void resetPassword(String email,
                              String answer,
                              String newPassword)
    {

        User user = findByEmail(email);


        // case-insensitive + trim safe check
        if (!user.getSecurityAnswer()
                .trim()
                .equalsIgnoreCase(answer.trim()))
        {

            throw new RuntimeException("Security answer is incorrect");

        }


        user.setPassword(
                passwordEncoder.encode(newPassword)
        );


        userRepository.save(user);

    }


}