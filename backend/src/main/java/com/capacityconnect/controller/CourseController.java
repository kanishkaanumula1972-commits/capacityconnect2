package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.course.CourseDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllPublishedCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CourseDto>> getMyCourses(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(courseService.getMyCoursesAsTrainer(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CourseDto dto
    ) {
        return ResponseEntity.ok(courseService.createCourse(principal.getId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody CourseDto dto
    ) {
        return ResponseEntity.ok(courseService.updateCourse(principal.getId(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        courseService.deleteCourse(principal.getId(), id);
        return ResponseEntity.ok(MessageResponse.builder().message("Course deleted").success(true).build());
    }
}