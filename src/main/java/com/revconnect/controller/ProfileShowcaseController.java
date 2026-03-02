package com.revconnect.controller;

import com.revconnect.dto.request.ShowcaseRequest;
import com.revconnect.dto.response.ShowcaseResponse;
import com.revconnect.service.ProfileShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileShowcaseController {

    private final ProfileShowcaseService profileShowcaseService;

    @PostMapping("/{userId}/showcase")
    public ResponseEntity<String> addShowcase(
            @PathVariable Long userId,
            @RequestBody ShowcaseRequest request
    ) {
        profileShowcaseService.addShowcase(userId, request);
        return ResponseEntity.ok("Showcase added successfully");
    }

    @GetMapping("/{userId}/showcase")
    public List<ShowcaseResponse> getShowcases(
            @PathVariable Long userId
    ) {
        return profileShowcaseService.getShowcases(userId);
    }

    @PutMapping("/{userId}/showcase/{showcaseId}")
    public ResponseEntity<String> updateShowcase(
            @PathVariable Long userId,
            @PathVariable Long showcaseId,
            @RequestBody ShowcaseRequest request
    ) {
        profileShowcaseService.updateShowcase(userId, showcaseId, request);
        return ResponseEntity.ok("Showcase updated successfully");
    }

    @DeleteMapping("/{userId}/showcase/{showcaseId}")
    public ResponseEntity<String> deleteShowcase(
            @PathVariable Long userId,
            @PathVariable Long showcaseId
    ) {
        profileShowcaseService.deleteShowcase(userId, showcaseId);
        return ResponseEntity.ok("Showcase deleted successfully");
    }
}