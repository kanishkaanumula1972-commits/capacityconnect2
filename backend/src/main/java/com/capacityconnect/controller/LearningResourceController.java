package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.course.LearningResourceDto;
import com.capacityconnect.entity.Course;
import com.capacityconnect.entity.LearningResource;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.LearningResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class LearningResourceController {

    private final LearningResourceService learningResourceService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<LearningResourceDto> uploadResource(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam LearningResource.ResourceType type,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Course.Difficulty difficulty,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(learningResourceService.uploadResource(
                principal.getId(), courseId, title, type, topic, difficulty, file
        ));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LearningResourceDto>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(learningResourceService.getResourcesByCourse(courseId));
    }

    // Section 14: Trainer Knowledge Library - filter across everything
    @GetMapping("/library")
    public ResponseEntity<List<LearningResourceDto>> browseLibrary(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Course.Difficulty difficulty,
            @RequestParam(required = false) LearningResource.ResourceType type
    ) {
        return ResponseEntity.ok(learningResourceService.browseLibrary(topic, difficulty, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteResource(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        learningResourceService.deleteResource(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Resource deleted").success(true).build());
    }
}