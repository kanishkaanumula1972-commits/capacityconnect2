package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.profile.QualificationDto;
import com.capacityconnect.dto.profile.TrainerProfileDto;
import com.capacityconnect.dto.profile.UpdateTrainerProfileRequest;
import com.capacityconnect.dto.profile.WorkExperienceDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.TrainerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerProfileController {

    private final TrainerProfileService trainerProfileService;

    @GetMapping("/profile")
    public ResponseEntity<TrainerProfileDto> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trainerProfileService.getProfile(principal.getId()));
    }

    // Public-ish endpoint: view any trainer's profile by ID (e.g. from trainer matching results)
    @GetMapping("/{id}")
    public ResponseEntity<TrainerProfileDto> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerProfileService.getProfileById(id));
    }

    @PutMapping("/profile")
    public ResponseEntity<TrainerProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateTrainerProfileRequest request
    ) {
        return ResponseEntity.ok(trainerProfileService.updateProfile(principal.getId(), request));
    }

    @PostMapping("/profile/qualifications")
    public ResponseEntity<QualificationDto> addQualification(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody QualificationDto dto
    ) {
        return ResponseEntity.ok(trainerProfileService.addQualification(principal.getId(), dto));
    }

    @DeleteMapping("/profile/qualifications/{id}")
    public ResponseEntity<MessageResponse> deleteQualification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        trainerProfileService.deleteQualification(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Qualification removed").success(true).build());
    }

    @PostMapping("/profile/experience")
    public ResponseEntity<WorkExperienceDto> addWorkExperience(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkExperienceDto dto
    ) {
        return ResponseEntity.ok(trainerProfileService.addWorkExperience(principal.getId(), dto));
    }

    @DeleteMapping("/profile/experience/{id}")
    public ResponseEntity<MessageResponse> deleteWorkExperience(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        trainerProfileService.deleteWorkExperience(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Work experience removed").success(true).build());
    }
}