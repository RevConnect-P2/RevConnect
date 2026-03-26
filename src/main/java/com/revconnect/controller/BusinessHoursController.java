package com.revconnect.controller;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class BusinessHoursController {

    //  LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(BusinessHoursController.class);

    private final ProfileService profileService;

    //  ADD business hours
    @PostMapping("/{userId}/business-hours")
    public ResponseEntity<String> addBusinessHours(
            @PathVariable Long userId,
            @RequestBody List<BusinessHoursRequest> request
    ) {

        logger.info("Adding business hours for user {}", userId);

        profileService.addBusinessHours(userId, request);

        logger.info("Business hours saved successfully for user {}", userId);

        return ResponseEntity.ok("Business hours saved successfully");
    }

    //  GET business hours
    @GetMapping("/{userId}/business-hours")
    public ResponseEntity<List<BusinessHoursResponse>> getBusinessHours(
            @PathVariable Long userId
    ) {

        logger.info("Fetching business hours for user {}", userId);

        return ResponseEntity.ok(
                profileService.getBusinessHours(userId)
        );
    }

    @PutMapping("/{userId}/business-hours/{dayOfWeek}")
    public ResponseEntity<String> updateBusinessHours(
            @PathVariable Long userId,
            @PathVariable String dayOfWeek,
            @RequestBody BusinessHoursRequest request
    ) {

        logger.info("Updating business hours for user {} on {}", userId, dayOfWeek);

        profileService.updateBusinessHours(userId, dayOfWeek, request);

        logger.info("Business hours updated for user {} on {}", userId, dayOfWeek);

        return ResponseEntity.ok("Business hours updated successfully");
    }

    @DeleteMapping("/{userId}/business-hours/{dayOfWeek}")
    public ResponseEntity<String> deleteBusinessHours(
            @PathVariable Long userId,
            @PathVariable String dayOfWeek
    ) {

        logger.info("Deleting business hours for user {} on {}", userId, dayOfWeek);

        profileService.deleteBusinessHours(userId, dayOfWeek);

        logger.info("Business hours deleted for user {} on {}", userId, dayOfWeek);

        return ResponseEntity.ok("Business hours deleted for " + dayOfWeek);
    }

    @DeleteMapping("/{userId}/business-hours")
    public ResponseEntity<String> deleteAllBusinessHours(
            @PathVariable Long userId
    ) {

        logger.info("Deleting all business hours for user {}", userId);

        profileService.deleteAllBusinessHours(userId);

        logger.info("All business hours deleted successfully for user {}", userId);

        return ResponseEntity.ok("All business hours deleted successfully");
    }
}