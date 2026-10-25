package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.profile.QualificationDto;
import com.capacityconnect.dto.profile.TraineeProfileDto;
import com.capacityconnect.dto.profile.TraineeSkillDto;
import com.capacityconnect.dto.profile.UpdateTraineeProfileRequest;
import com.capacityconnect.dto.profile.WorkExperienceDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.TraineeProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
public class TraineeProfileController {

    private final TraineeProfileService traineeProfileService;

    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileDto> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(traineeProfileService.getProfile(principal.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<TraineeProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateTraineeProfileRequest request
    ) {
        return ResponseEntity.ok(traineeProfileService.updateProfile(principal.getId(), request));
    }

    @PostMapping("/profile/qualifications")
    public ResponseEntity<QualificationDto> addQualification(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody QualificationDto dto
    ) {
        return ResponseEntity.ok(traineeProfileService.addQualification(principal.getId(), dto));
    }

    @DeleteMapping("/profile/qualifications/{id}")
    public ResponseEntity<MessageResponse> deleteQualification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        traineeProfileService.deleteQualification(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Qualification removed").success(true).build());
    }

    @PostMapping("/profile/experience")
    public ResponseEntity<WorkExperienceDto> addWorkExperience(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkExperienceDto dto
    ) {
        return ResponseEntity.ok(traineeProfileService.addWorkExperience(principal.getId(), dto));
    }

    @DeleteMapping("/profile/experience/{id}")
    public ResponseEntity<MessageResponse> deleteWorkExperience(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        traineeProfileService.deleteWorkExperience(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Work experience removed").success(true).build());
    }

    @PostMapping("/profile/skills")
    public ResponseEntity<TraineeSkillDto> addOrUpdateSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TraineeSkillDto dto
    ) {
        return ResponseEntity.ok(traineeProfileService.addOrUpdateSkill(principal.getId(), dto));
    }

    @DeleteMapping("/profile/skills/{id}")
    public ResponseEntity<MessageResponse> deleteSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        traineeProfileService.deleteSkill(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Skill removed").success(true).build());
    }
}