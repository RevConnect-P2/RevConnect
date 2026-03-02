package com.revconnect.repository;

import com.revconnect.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser_UserId(Long userId);

    List<UserProfile> findByUser_UsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String username,
            String fullName
    );
}