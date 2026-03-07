package com.revconnect.service;

import com.revconnect.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User getUserById(Long userId);

    // Returns userId for a given username
    Long getUserIdByUsername(String username);

    // Optional: safer approach to get the User object
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    User getUserByEmailOrThrow(String email);

    List<User> getAllOtherUsers(Long userId);

    // Throw exception if user not found
    User getUserByUsernameOrThrow(String username);
    User getUserByIdOrThrow(Long userId);
}