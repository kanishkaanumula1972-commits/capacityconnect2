package com.capacityconnect.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Section 24: Admin Dashboard summary cards - matches the screenshot
 * (Total Trainees, Total Trainers, Active Courses, Enrollments, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDto {
    private long totalTrainees;
    private long totalTrainers;
    private long activeCourses;
    private long totalEnrollments;
    private long completedEnrollments;
    private long totalCertificates;
    private long totalAssessments;
    private double averageAssessmentScore;
    private long pendingApprovals;
}