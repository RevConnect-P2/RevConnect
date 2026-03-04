package com.revconnect.service;

import com.revconnect.entity.User;

public interface UserService {
    User findByEmail(String email);
    User findById(Long id);
}

    Long getUserIdByUsername(String username);

}
