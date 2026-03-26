package com.revconnect.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String email;

    private String username;

    private String password;


    private String userType;


    private String securityQuestion;

    private String securityAnswer;

}