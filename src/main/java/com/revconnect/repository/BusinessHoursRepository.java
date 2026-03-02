package com.revconnect.repository;

import com.revconnect.entity.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessHoursRepository
        extends JpaRepository<BusinessHours, Long> {

    List<BusinessHours> findByProfile_ProfileId(Long profileId);

    void deleteByProfile_ProfileId(Long profileId);
}