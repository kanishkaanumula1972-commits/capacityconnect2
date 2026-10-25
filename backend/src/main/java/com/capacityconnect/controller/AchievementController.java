package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.notification.AchievementDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.AchievementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/api/public/achievements")
    public ResponseEntity<List<AchievementDto>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @PostMapping("/api/admin/achievements")
    public ResponseEntity<AchievementDto> createAchievement(@Valid @RequestBody AchievementDto dto) {
        return ResponseEntity.ok(achievementService.createAchievement(dto));
    }

    @DeleteMapping("/api/admin/achievements/{id}")
    public ResponseEntity<MessageResponse> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Achievement deleted").success(true).build());
    }

    @GetMapping("/api/trainees/achievements")
    public ResponseEntity<List<AchievementDto>> getMyAchievements(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(achievementService.getMyAchievements(principal.getId()));
    }
}