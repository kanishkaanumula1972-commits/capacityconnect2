package com.capacityconnect.service;

import com.capacityconnect.dto.assessment.AttemptResultDto;
import com.capacityconnect.dto.assessment.AttemptSubmissionDto;
import com.capacityconnect.entity.*;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements section 16: Assessment -> Competency Update.
 *
 * When a trainee submits an assessment tied to a targetSkill, their
 * TraineeSkill.score is updated using a weighted blend of their old
 * score and their new assessment performance - not a blind overwrite,
 * so one lucky/unlucky attempt doesn't wildly swing a well-established
 * skill level, but still moves meaningfully toward the new evidence.
 */
@Service
@RequiredArgsConstructor
public class AssessmentAttemptService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final TraineeSkillRepository traineeSkillRepository;
    private final AchievementService achievementService;

    // How much weight the new assessment score carries vs the trainee's
    // existing skill score. 0.6 means: new evidence dominates but doesn't
    // fully erase prior standing in one shot.
    private static final double NEW_SCORE_WEIGHT = 0.6;

    @Transactional
    public AttemptResultDto submitAttempt(Long userId, Long assessmentId, AttemptSubmissionDto submission) {
        TraineeProfile profile = traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (assessment.getDeadline() != null && java.time.LocalDateTime.now().isAfter(assessment.getDeadline())) {
            throw new BadRequestException("This assessment's deadline has passed");
        }

        List<Question> questions = questionRepository.findByAssessmentId(assessmentId);
        if (questions.isEmpty()) {
            throw new BadRequestException("This assessment has no questions");
        }

        Map<Long, String> answers = submission.getAnswers() != null ? submission.getAnswers() : Map.of();

        List<AttemptResultDto.QuestionResultDto> questionResults = new ArrayList<>();
        int marksObtained = 0;
        int totalMarks = 0;

        StringBuilder answersLog = new StringBuilder();

        for (Question q : questions) {
            totalMarks += q.getMarks();
            String selected = answers.get(q.getId());
            boolean correct = selected != null && selected.equalsIgnoreCase(q.getCorrectOption());
            if (correct) {
                marksObtained += q.getMarks();
            }

            questionResults.add(AttemptResultDto.QuestionResultDto.builder()
                    .questionId(q.getId())
                    .questionText(q.getQuestionText())
                    .selectedOption(selected)
                    .correctOption(q.getCorrectOption())
                    .isCorrect(correct)
                    .build());

            if (answersLog.length() > 0) answersLog.append(",");
            answersLog.append(q.getId()).append(":").append(selected != null ? selected : "-");
        }

        double percentageScore = totalMarks > 0 ? (marksObtained * 100.0) / totalMarks : 0.0;
        percentageScore = Math.round(percentageScore * 10.0) / 10.0;

        // ---------- Competency Update (section 16) ----------
        AttemptResultDto.CompetencyUpdateDto competencyUpdateDto = null;
        Double scoreBeforeSnapshot = null;
        Double scoreAfterSnapshot = null;

        if (assessment.getTargetSkill() != null) {
            Skill targetSkill = assessment.getTargetSkill();

            TraineeSkill traineeSkill = traineeSkillRepository
                    .findByTraineeProfileIdAndSkillId(profile.getId(), targetSkill.getId())
                    .orElse(TraineeSkill.builder()
                            .traineeProfile(profile)
                            .skill(targetSkill)
                            .proficiencyLevel(TraineeSkill.ProficiencyLevel.BEGINNER)
                            .score(0.0)
                            .build());

            double before = traineeSkill.getScore();
            // Weighted blend: new evidence (the assessment score) dominates,
            // but doesn't completely erase prior standing.
            double after = (before * (1 - NEW_SCORE_WEIGHT)) + (percentageScore * NEW_SCORE_WEIGHT);
            after = Math.round(after * 10.0) / 10.0;

            traineeSkill.setScore(after);
            traineeSkill.setProficiencyLevel(scoreToProficiencyLevel(after));
            traineeSkillRepository.save(traineeSkill);

            achievementService.checkAndAwardSkillMastery(profile, after);

            scoreBeforeSnapshot = before;
            scoreAfterSnapshot = after;

            competencyUpdateDto = AttemptResultDto.CompetencyUpdateDto.builder()
                    .skillName(targetSkill.getName())
                    .scoreBefore(before)
                    .scoreAfter(after)
                    .improvement(Math.round((after - before) * 10.0) / 10.0)
                    .build();
        }

        // ---------- Persist the attempt (audit trail) ----------
        AssessmentAttempt attempt = AssessmentAttempt.builder()
                .traineeProfile(profile)
                .assessment(assessment)
                .submittedAnswers(answersLog.toString())
                .marksObtained(marksObtained)
                .totalMarks(totalMarks)
                .percentageScore(percentageScore)
                .skillScoreBefore(scoreBeforeSnapshot)
                .skillScoreAfter(scoreAfterSnapshot)
                .build();
        attempt = assessmentAttemptRepository.save(attempt);
        achievementService.checkAndAwardAssessmentScore(profile, percentageScore);

        return AttemptResultDto.builder()
                .attemptId(attempt.getId())
                .marksObtained(marksObtained)
                .totalMarks(totalMarks)
                .percentageScore(percentageScore)
                .questionResults(questionResults)
                .competencyUpdate(competencyUpdateDto)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AttemptResultDto> getMyAttempts(Long userId) {
        TraineeProfile profile = traineeProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee profile not found"));

        return assessmentAttemptRepository.findByTraineeProfileId(profile.getId())
                .stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    private AttemptResultDto toSummaryDto(AssessmentAttempt attempt) {
        AttemptResultDto.CompetencyUpdateDto updateDto = null;
        if (attempt.getSkillScoreBefore() != null && attempt.getSkillScoreAfter() != null) {
            updateDto = AttemptResultDto.CompetencyUpdateDto.builder()
                    .skillName(attempt.getAssessment().getTargetSkill() != null
                            ? attempt.getAssessment().getTargetSkill().getName() : null)
                    .scoreBefore(attempt.getSkillScoreBefore())
                    .scoreAfter(attempt.getSkillScoreAfter())
                    .improvement(Math.round((attempt.getSkillScoreAfter() - attempt.getSkillScoreBefore()) * 10.0) / 10.0)
                    .build();
        }

        return AttemptResultDto.builder()
                .attemptId(attempt.getId())
                .marksObtained(attempt.getMarksObtained())
                .totalMarks(attempt.getTotalMarks())
                .percentageScore(attempt.getPercentageScore())
                .competencyUpdate(updateDto)
                .build();
    }

    private TraineeSkill.ProficiencyLevel scoreToProficiencyLevel(double score) {
        if (score >= 90) return TraineeSkill.ProficiencyLevel.EXPERT;
        if (score >= 70) return TraineeSkill.ProficiencyLevel.ADVANCED;
        if (score >= 40) return TraineeSkill.ProficiencyLevel.INTERMEDIATE;
        return TraineeSkill.ProficiencyLevel.BEGINNER;
    }
}



//traineeSkill.setProficiencyLevel(scoreToProficiencyLevel(after));

//AssessmentAttempt attempt =

//boolean correct = selected != null && selected.equalsIgnoreCase(q.getCorrectOption());