package com.capacityconnect.service;

import com.capacityconnect.dto.assessment.AssessmentDto;
import com.capacityconnect.dto.assessment.QuestionDto;
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
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final CourseRepository courseRepository;
    private final SkillRepository skillRepository;
    private final CompetencyRepository competencyRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private TrainerProfile getTrainerProfile(Long userId) {
        return trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer profile not found"));
    }

    @Transactional
    public AssessmentDto createAssessment(Long userId, AssessmentDto dto) {
        TrainerProfile trainer = getTrainerProfile(userId);

        if (dto.getQuestions() == null || dto.getQuestions().isEmpty()) {
            throw new BadRequestException("An assessment must have at least one question");
        }

        Course course = null;
        if (dto.getCourseId() != null) {
            course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        }

        Skill targetSkill = null;
        if (dto.getTargetSkillId() != null) {
            targetSkill = skillRepository.findById(dto.getTargetSkillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target skill not found"));
        }

        Competency targetCompetency = null;
        if (dto.getTargetCompetencyId() != null) {
            targetCompetency = competencyRepository.findById(dto.getTargetCompetencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target competency not found"));
        }

        int totalMarks = dto.getQuestions().stream()
                .mapToInt(q -> q.getMarks() != null ? q.getMarks() : 1)
                .sum();

        Assessment assessment = Assessment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .course(course)
                .targetSkill(targetSkill)
                .targetCompetency(targetCompetency)
                .createdBy(trainer)
                .totalMarks(totalMarks)
                .durationMinutes(dto.getDurationMinutes())
                .deadline(dto.getDeadline())
                .build();
        assessment = assessmentRepository.save(assessment);

        for (QuestionDto qDto : dto.getQuestions()) {
            Question question = Question.builder()
                    .assessment(assessment)
                    .questionText(qDto.getQuestionText())
                    .optionA(qDto.getOptionA())
                    .optionB(qDto.getOptionB())
                    .optionC(qDto.getOptionC())
                    .optionD(qDto.getOptionD())
                    .correctOption(qDto.getCorrectOption())
                    .marks(qDto.getMarks() != null ? qDto.getMarks() : 1)
                    .build();
            questionRepository.save(question);
        }

        return toDto(assessment, false, true);
    }

    @Transactional(readOnly = true)
    public List<AssessmentDto> getMyCreatedAssessments(Long userId) {
        TrainerProfile trainer = getTrainerProfile(userId);
        return assessmentRepository.findByCreatedById(trainer.getId())
                .stream().map(a -> toDto(a, false, true)).collect(Collectors.toList());
    }

    /**
     * Trainee-facing view - correctOption is stripped from every question
     * so the frontend can never see or leak the answer key before submission.
     */
    @Transactional(readOnly = true)
    public AssessmentDto getAssessmentForAttempt(Long traineeUserId, Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        return toDto(assessment, true, false);
    }

    @Transactional(readOnly = true)
    public List<AssessmentDto> getAssessmentsByCourse(Long courseId) {
        return assessmentRepository.findByCourseId(courseId)
                .stream().map(a -> toDto(a, true, false)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAssessment(Long userId, Long assessmentId) {
        TrainerProfile trainer = getTrainerProfile(userId);
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getCreatedBy().getId().equals(trainer.getId())) {
            throw new BadRequestException("You do not own this assessment");
        }
        assessmentRepository.delete(assessment);
    }

    private AssessmentDto toDto(Assessment assessment, boolean hideAnswers, boolean includeAnswers) {
        List<Question> questions = questionRepository.findByAssessmentId(assessment.getId());

        List<QuestionDto> questionDtos = questions.stream()
                .map(q -> QuestionDto.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        // Critical security line: never send correctOption to a trainee
                        // taking the assessment; only trainer-facing views get it.
                        .correctOption(hideAnswers ? null : q.getCorrectOption())
                        .marks(q.getMarks())
                        .build())
                .collect(Collectors.toList());

        return AssessmentDto.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .courseId(assessment.getCourse() != null ? assessment.getCourse().getId() : null)
                .courseTitle(assessment.getCourse() != null ? assessment.getCourse().getTitle() : null)
                .targetSkillId(assessment.getTargetSkill() != null ? assessment.getTargetSkill().getId() : null)
                .targetSkillName(assessment.getTargetSkill() != null ? assessment.getTargetSkill().getName() : null)
                .targetCompetencyId(assessment.getTargetCompetency() != null ? assessment.getTargetCompetency().getId() : null)
                .targetCompetencyName(assessment.getTargetCompetency() != null ? assessment.getTargetCompetency().getName() : null)
                .totalMarks(assessment.getTotalMarks())
                .durationMinutes(assessment.getDurationMinutes())
                .deadline(assessment.getDeadline())
                .questions(questionDtos)
                .build();
    }
}