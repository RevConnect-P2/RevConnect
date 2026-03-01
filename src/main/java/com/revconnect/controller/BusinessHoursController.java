package com.revconnect.controller;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class BusinessHoursController {

    private final ProfileService profileService;

    // ✅ ADD business hours
    @PostMapping("/{userId}/business-hours")
    public ResponseEntity<String> addBusinessHours(
            @PathVariable Long userId,
            @RequestBody List<BusinessHoursRequest> request
    ) {
        profileService.addBusinessHours(userId, request);
        return ResponseEntity.ok("Business hours saved successfully");
    }

    // ✅ GET business hours
    @GetMapping("/{userId}/business-hours")
    public ResponseEntity<List<BusinessHoursResponse>> getBusinessHours(
            @PathVariable Long userId
    ) {
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
        profileService.updateBusinessHours(userId, dayOfWeek, request);
        return ResponseEntity.ok("Business hours updated successfully");
    }

    @DeleteMapping("/{userId}/business-hours/{dayOfWeek}")
    public ResponseEntity<String> deleteBusinessHours(
            @PathVariable Long userId,
            @PathVariable String dayOfWeek
    ) {
        profileService.deleteBusinessHours(userId, dayOfWeek);
        return ResponseEntity.ok("Business hours deleted for " + dayOfWeek);
    }

    @DeleteMapping("/{userId}/business-hours")
    public ResponseEntity<String> deleteAllBusinessHours(
            @PathVariable Long userId
    ) {
        profileService.deleteAllBusinessHours(userId);
        return ResponseEntity.ok("All business hours deleted successfully");
    }
}