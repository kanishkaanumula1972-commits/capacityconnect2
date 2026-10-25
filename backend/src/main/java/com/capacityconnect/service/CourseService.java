package com.capacityconnect.service;

import com.capacityconnect.dto.course.CourseDto;
import com.capacityconnect.dto.course.CourseSkillDto;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseSkillRepository courseSkillRepository;
    private final CourseCompetencyRepository courseCompetencyRepository;
    private final SkillRepository skillRepository;
    private final CompetencyRepository competencyRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FeedbackRepository feedbackRepository;

    private TrainerProfile getTrainerProfile(Long userId) {
        return trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found"));
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getAllPublishedCourses() {
        return courseRepository.findByStatus(Course.CourseStatus.PUBLISHED)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return toDto(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getMyCoursesAsTrainer(Long userId) {
        TrainerProfile trainer = getTrainerProfile(userId);
        return courseRepository.findByTrainerId(trainer.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CourseDto createCourse(Long userId, CourseDto dto) {
        TrainerProfile trainer = getTrainerProfile(userId);

        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .difficulty(dto.getDifficulty())
                .durationHours(dto.getDurationHours())
                .thumbnailUrl(dto.getThumbnailUrl())
                .trainer(trainer)
                .status(Course.CourseStatus.PUBLISHED)
                .build();
        course = courseRepository.save(course);

        attachSkillsAndCompetencies(course, dto);

        return toDto(courseRepository.findById(course.getId()).orElseThrow());
    }

    @Transactional
    public CourseDto updateCourse(Long userId, Long courseId, CourseDto dto) {
        TrainerProfile trainer = getTrainerProfile(userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getTrainer().getId().equals(trainer.getId())) {
            throw new BadRequestException("You do not own this course");
        }

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCategory(dto.getCategory());
        course.setDifficulty(dto.getDifficulty());
        course.setDurationHours(dto.getDurationHours());
        if (dto.getThumbnailUrl() != null) course.setThumbnailUrl(dto.getThumbnailUrl());
        courseRepository.save(course);

        courseSkillRepository.deleteAll(courseSkillRepository.findByCourseId(courseId));
        courseSkillRepository.flush();
        courseCompetencyRepository.deleteAll(courseCompetencyRepository.findByCourseId(courseId));
        courseCompetencyRepository.flush();
        attachSkillsAndCompetencies(course, dto);

        return toDto(courseRepository.findById(courseId).orElseThrow());
    }

    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        TrainerProfile trainer = getTrainerProfile(userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getTrainer().getId().equals(trainer.getId())) {
            throw new BadRequestException("You do not own this course");
        }
        courseRepository.delete(course);
    }

    private void attachSkillsAndCompetencies(Course course, CourseDto dto) {
        if (dto.getSkillsCovered() != null) {
            for (CourseSkillDto skillDto : dto.getSkillsCovered()) {
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillDto.getSkillId()));
                CourseSkill courseSkill = CourseSkill.builder()
                        .course(course)
                        .skill(skill)
                        .expectedImprovement(skillDto.getExpectedImprovement() != null ? skillDto.getExpectedImprovement() : 20.0)
                        .build();
                courseSkillRepository.save(courseSkill);
            }
        }

        if (dto.getCompetencyIds() != null) {
            for (Long competencyId : dto.getCompetencyIds()) {
                Competency competency = competencyRepository.findById(competencyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Competency not found: " + competencyId));
                CourseCompetency courseCompetency = CourseCompetency.builder()
                        .course(course)
                        .competency(competency)
                        .build();
                courseCompetencyRepository.save(courseCompetency);
            }
        }
    }

    private CourseDto toDto(Course course) {
        List<CourseSkillDto> skills = courseSkillRepository.findByCourseId(course.getId())
                .stream()
                .map(cs -> CourseSkillDto.builder()
                        .skillId(cs.getSkill().getId())
                        .skillName(cs.getSkill().getName())
                        .expectedImprovement(cs.getExpectedImprovement())
                        .build())
                .collect(Collectors.toList());

        List<CourseCompetency> courseCompetencies = courseCompetencyRepository.findByCourseId(course.getId());
        List<Long> competencyIds = courseCompetencies.stream()
                .map(cc -> cc.getCompetency().getId()).collect(Collectors.toList());
        List<String> competencyNames = courseCompetencies.stream()
                .map(cc -> cc.getCompetency().getName()).collect(Collectors.toList());

        long enrolledCount = enrollmentRepository.countByCourseId(course.getId());

        List<Feedback> feedbackList = feedbackRepository.findByCourseId(course.getId());
        double avgRating = feedbackList.stream().mapToDouble(Feedback::getRating).average().orElse(0.0);

        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .difficulty(course.getDifficulty())
                .durationHours(course.getDurationHours())
                .thumbnailUrl(course.getThumbnailUrl())
                .trainerId(course.getTrainer().getId())
                .trainerName(course.getTrainer().getUser().getFullName())
                .skillsCovered(skills)
                .competencyIds(competencyIds)
                .competencyNames(competencyNames)
                .status(course.getStatus())
                .enrolledCount(enrolledCount)
                .averageRating(Math.round(avgRating * 10.0) / 10.0)
                .build();
    }
}