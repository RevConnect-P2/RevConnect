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

    // ✅ ADD THIS
    private String userType;

    // ✅ ADD THIS
    private String securityQuestion;

    // ✅ ADD THIS
    private String securityAnswer;

}