package com.revconnect.service;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;

public interface AuthService {


    // REGISTER USER
    User register(RegisterRequest request);



    // LOGIN USER
    User login(LoginRequest request);

    User findByEmail(String email);

    void resetPassword(String email,
                       String answer,
                       String newPassword);


}