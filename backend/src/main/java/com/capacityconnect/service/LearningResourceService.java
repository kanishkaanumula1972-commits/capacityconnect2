package com.capacityconnect.service;

import com.capacityconnect.dto.course.LearningResourceDto;
import com.capacityconnect.entity.Course;
import com.capacityconnect.entity.LearningResource;
import com.capacityconnect.entity.TrainerProfile;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.CourseRepository;
import com.capacityconnect.repository.LearningResourceRepository;
import com.capacityconnect.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;
    private final CourseRepository courseRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public LearningResourceDto uploadResource(
            Long userId,
            Long courseId,
            String title,
            LearningResource.ResourceType type,
            String topic,
            Course.Difficulty difficulty,
            MultipartFile file
    ) {
        TrainerProfile trainer = trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getTrainer().getId().equals(trainer.getId())) {
            throw new BadRequestException("You do not own this course");
        }

        String fileUrl = fileStorageService.store(file, "resources");

        LearningResource resource = LearningResource.builder()
                .title(title)
                .type(type)
                .fileUrl(fileUrl)
                .topic(topic)
                .difficulty(difficulty)
                .course(course)
                .build();
        resource = learningResourceRepository.save(resource);

        return toDto(resource);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDto> getResourcesByCourse(Long courseId) {
        return learningResourceRepository.findByCourseId(courseId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Section 14: Trainer Knowledge Library - filterable across ALL resources,
     * not scoped to one course. Any filter param left null is ignored.
     */
    @Transactional(readOnly = true)
    public List<LearningResourceDto> browseLibrary(String topic, Course.Difficulty difficulty, LearningResource.ResourceType type) {
        List<LearningResource> resources = learningResourceRepository.findAll();

        return resources.stream()
                .filter(r -> topic == null || topic.equalsIgnoreCase(r.getTopic()))
                .filter(r -> difficulty == null || difficulty.equals(r.getDifficulty()))
                .filter(r -> type == null || type.equals(r.getType()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteResource(Long userId, Long resourceId) {
        TrainerProfile trainer = trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found"));

        LearningResource resource = learningResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (!resource.getCourse().getTrainer().getId().equals(trainer.getId())) {
            throw new BadRequestException("You do not own this resource");
        }
        learningResourceRepository.delete(resource);
    }

    private LearningResourceDto toDto(LearningResource r) {
        return LearningResourceDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .type(r.getType())
                .fileUrl(r.getFileUrl())
                .topic(r.getTopic())
                .difficulty(r.getDifficulty())
                .courseId(r.getCourse().getId())
                .courseTitle(r.getCourse().getTitle())
                .trainerName(r.getCourse().getTrainer().getUser().getFullName())
                .uploadedAt(r.getUploadedAt())
                .build();
    }
}