package com.revconnect.repository;

import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // check email exists
    Optional<User> findByEmail(String email);

    // check username exists
    Optional<User> findByUsername(String username);

    // 🔍 SEARCH USERS BY USERNAME (for search bar)
    List<User> findByUsernameContainingIgnoreCase(String keyword);

}