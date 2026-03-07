package com.revconnect.repository;

import com.revconnect.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long>
{


    // check email exists
    Optional<User> findByEmail(String email);



    // ✅ ADD THIS (IMPORTANT)
    // check username exists
    Optional<User> findByUsername(String username);

    // ✅ Fetch all users except the given userId
    List<User> findByUserIdNot(Long userId);
}



