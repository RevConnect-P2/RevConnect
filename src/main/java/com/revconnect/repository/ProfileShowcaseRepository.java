package com.revconnect.repository;

import com.revconnect.entity.ProfileShowcase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileShowcaseRepository
        extends JpaRepository<ProfileShowcase, Long> {

    List<ProfileShowcase> findByProfile_ProfileId(Long profileId);

    Optional<ProfileShowcase> findByShowcaseIdAndProfile_ProfileId(
            Long showcaseId,
            Long profileId
    );
}