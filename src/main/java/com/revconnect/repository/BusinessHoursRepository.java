package com.revconnect.repository;

import com.revconnect.entity.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BusinessHoursRepository
        extends JpaRepository<BusinessHours, Long> {

    List<BusinessHours> findByProfile_ProfileId(Long profileId);

    @Modifying
    @Transactional
    void deleteByProfile_ProfileId(Long profileId);
}