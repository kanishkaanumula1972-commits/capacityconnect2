package com.capacityconnect.config;

import com.capacityconnect.entity.*;
import com.capacityconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final SkillRepository skillRepository;
    private final TraineeSkillRepository traineeSkillRepository;
    private final CompetencyRepository competencyRepository;
    private final CompetencySkillRepository competencySkillRepository;
    private final TraineeCompetencyRepository traineeCompetencyRepository;
    private final TrainerExpertiseRepository trainerExpertiseRepository;
    private final TrainerCertificationRepository trainerCertificationRepository;
    private final CourseRepository courseRepository;
    private final CourseSkillRepository courseSkillRepository;
    private final CourseCompetencyRepository courseCompetencyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final FeedbackRepository feedbackRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AchievementRepository achievementRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("Data already seeded, skipping DataSeeder.");
            return;
        }

        System.out.println("Seeding demo data for Capacity Connect...");

        List<Skill> skills = seedSkills();
        List<Competency> competencies = seedCompetencies(skills);
        seedAdmin();

        List<TrainerProfile> trainers = seedTrainers(skills);
        List<TraineeProfile> trainees = seedTrainees(skills, competencies);

        List<Course> courses = seedCourses(trainers, skills, competencies);
        seedEnrollmentsAndFeedback(trainees, courses);

        seedCertificates(trainees, skills);
        List<Assessment> assessments = seedAssessments(trainers, courses, skills);
        seedAchievements();

        System.out.println("Seeding complete: " + trainees.size() + " trainees, " + trainers.size()
                + " trainers, " + courses.size() + " courses, " + assessments.size() + " assessments.");
    }

    // ==================== SKILLS ====================

    private List<Skill> seedSkills() {
        String[][] skillData = {
                {"Java", "Programming Language"},
                {"Spring Boot", "Framework"},
                {"REST API", "Backend"},
                {"SQL", "Database"},
                {"Git", "Tools"},
                {"Cloud Computing", "Cloud"},
                {"AWS", "Cloud"},
                {"Cybersecurity", "Security"},
                {"AI/ML", "Data Science"},
                {"Python", "Programming Language"},
                {"React", "Frontend"},
                {"JavaScript", "Programming Language"},
                {"Docker", "DevOps"},
                {"Kubernetes", "DevOps"},
                {"Data Analysis", "Data Science"},
                {"Communication", "Soft Skill"},
                {"Project Management", "Management"}
        };

        List<Skill> saved = new ArrayList<>();
        for (String[] data : skillData) {
            Skill skill = Skill.builder()
                    .name(data[0])
                    .category(data[1])
                    .description(data[0] + " skill")
                    .build();
            saved.add(skillRepository.save(skill));
        }
        return saved;
    }

    private Skill findSkill(List<Skill> skills, String name) {
        return skills.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Seed skill not found: " + name));
    }

    // ==================== COMPETENCIES ====================

    private List<Competency> seedCompetencies(List<Skill> skills) {
        List<Competency> competencies = new ArrayList<>();

        Competency backendDev = competencyRepository.save(Competency.builder()
                .name("Backend Developer")
                .description("Server-side application development using Java and Spring Boot")
                .category("Software Development")
                .build());
        saveRequiredSkill(backendDev, findSkill(skills, "Java"), 80.0);
        saveRequiredSkill(backendDev, findSkill(skills, "Spring Boot"), 70.0);
        saveRequiredSkill(backendDev, findSkill(skills, "REST API"), 70.0);
        saveRequiredSkill(backendDev, findSkill(skills, "SQL"), 60.0);
        saveRequiredSkill(backendDev, findSkill(skills, "Git"), 50.0);
        competencies.add(backendDev);

        Competency cloudEngineer = competencyRepository.save(Competency.builder()
                .name("Cloud Engineer")
                .description("Designing and managing cloud infrastructure")
                .category("Cloud & DevOps")
                .build());
        saveRequiredSkill(cloudEngineer, findSkill(skills, "Cloud Computing"), 75.0);
        saveRequiredSkill(cloudEngineer, findSkill(skills, "AWS"), 70.0);
        saveRequiredSkill(cloudEngineer, findSkill(skills, "Docker"), 60.0);
        saveRequiredSkill(cloudEngineer, findSkill(skills, "Kubernetes"), 55.0);
        competencies.add(cloudEngineer);

        Competency dataAnalyst = competencyRepository.save(Competency.builder()
                .name("Data Analyst")
                .description("Analyzing data to extract organizational insights")
                .category("Data & Analytics")
                .build());
        saveRequiredSkill(dataAnalyst, findSkill(skills, "Python"), 70.0);
        saveRequiredSkill(dataAnalyst, findSkill(skills, "Data Analysis"), 75.0);
        saveRequiredSkill(dataAnalyst, findSkill(skills, "SQL"), 65.0);
        saveRequiredSkill(dataAnalyst, findSkill(skills, "AI/ML"), 50.0);
        competencies.add(dataAnalyst);

        Competency frontendDev = competencyRepository.save(Competency.builder()
                .name("Frontend Developer")
                .description("Building modern responsive user interfaces")
                .category("Software Development")
                .build());
        saveRequiredSkill(frontendDev, findSkill(skills, "React"), 75.0);
        saveRequiredSkill(frontendDev, findSkill(skills, "JavaScript"), 75.0);
        saveRequiredSkill(frontendDev, findSkill(skills, "Git"), 50.0);
        competencies.add(frontendDev);

        Competency cyberAnalyst = competencyRepository.save(Competency.builder()
                .name("Cybersecurity Analyst")
                .description("Protecting organizational systems and data")
                .category("Security")
                .build());
        saveRequiredSkill(cyberAnalyst, findSkill(skills, "Cybersecurity"), 80.0);
        saveRequiredSkill(cyberAnalyst, findSkill(skills, "Cloud Computing"), 55.0);
        saveRequiredSkill(cyberAnalyst, findSkill(skills, "Communication"), 50.0);
        competencies.add(cyberAnalyst);

        Competency devOps = competencyRepository.save(Competency.builder()
                .name("DevOps Engineer")
                .description("CI/CD, containerization, and infrastructure automation")
                .category("Cloud & DevOps")
                .build());
        saveRequiredSkill(devOps, findSkill(skills, "Docker"), 75.0);
        saveRequiredSkill(devOps, findSkill(skills, "Kubernetes"), 70.0);
        saveRequiredSkill(devOps, findSkill(skills, "Git"), 65.0);
        saveRequiredSkill(devOps, findSkill(skills, "Cloud Computing"), 65.0);
        competencies.add(devOps);

        Competency mlEngineer = competencyRepository.save(Competency.builder()
                .name("AI/ML Engineer")
                .description("Building and deploying machine learning models")
                .category("Data & Analytics")
                .build());
        saveRequiredSkill(mlEngineer, findSkill(skills, "Python"), 80.0);
        saveRequiredSkill(mlEngineer, findSkill(skills, "AI/ML"), 75.0);
        saveRequiredSkill(mlEngineer, findSkill(skills, "Data Analysis"), 65.0);
        competencies.add(mlEngineer);

        Competency projectManager = competencyRepository.save(Competency.builder()
                .name("Project Manager")
                .description("Leading teams and coordinating project delivery")
                .category("Management")
                .build());
        saveRequiredSkill(projectManager, findSkill(skills, "Project Management"), 80.0);
        saveRequiredSkill(projectManager, findSkill(skills, "Communication"), 75.0);
        saveRequiredSkill(projectManager, findSkill(skills, "Git"), 40.0);
        competencies.add(projectManager);

        return competencies;
    }

    private void saveRequiredSkill(Competency competency, Skill skill, double requiredScore) {
        competencySkillRepository.save(CompetencySkill.builder()
                .competency(competency)
                .skill(skill)
                .requiredScore(requiredScore)
                .build());
    }

    // ==================== ADMIN ====================

    private void seedAdmin() {
        User admin = User.builder()
                .fullName("System Admin")
                .email("admin@capacityconnect.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .status(User.AccountStatus.ACTIVE)
                .enabled(true)
                .build();
        userRepository.save(admin);
    }

    // ==================== TRAINERS ====================

    private List<TrainerProfile> seedTrainers(List<Skill> skills) {
        List<TrainerProfile> trainers = new ArrayList<>();

        Object[][] trainerData = {
                {"Rahul Kumar", "rahul.kumar@capacityconnect.com", 8,
                        new String[]{"Java", "Spring Boot"},
                        new Object[][]{{"Java", "EXPERT"}, {"Spring Boot", "EXPERT"}, {"REST API", "ADVANCED"}},
                        4, 4.8, 42},
                {"Priya Sharma", "priya.sharma@capacityconnect.com", 5,
                        new String[]{"Spring Boot", "REST API"},
                        new Object[][]{{"Spring Boot", "ADVANCED"}, {"REST API", "ADVANCED"}, {"Git", "INTERMEDIATE"}},
                        2, 4.5, 30},
                {"Arjun Verma", "arjun.verma@capacityconnect.com", 6,
                        new String[]{"Cloud Computing", "AWS"},
                        new Object[][]{{"Cloud Computing", "EXPERT"}, {"AWS", "EXPERT"}, {"Docker", "ADVANCED"}},
                        5, 4.3, 25},
                {"Sneha Reddy", "sneha.reddy@capacityconnect.com", 4,
                        new String[]{"Python", "AI/ML"},
                        new Object[][]{{"Python", "ADVANCED"}, {"AI/ML", "ADVANCED"}, {"Data Analysis", "EXPERT"}},
                        3, 4.6, 20},
                {"Vikram Singh", "vikram.singh@capacityconnect.com", 7,
                        new String[]{"Cybersecurity", "Cloud Computing"},
                        new Object[][]{{"Cybersecurity", "EXPERT"}, {"Cloud Computing", "ADVANCED"}, {"Communication", "ADVANCED"}},
                        4, 4.7, 35}
        };

        for (Object[] data : trainerData) {
            String name = (String) data[0];
            String email = (String) data[1];
            int years = (int) data[2];
            String[] subjects = (String[]) data[3];
            Object[][] expertise = (Object[][]) data[4];
            int certCount = (int) data[5];
            double rating = (double) data[6];
            int totalRatings = (int) data[7];

            User user = userRepository.save(User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode("trainer123"))
                    .role(Role.TRAINER)
                    .status(User.AccountStatus.ACTIVE)
                    .enabled(true)
                    .build());

            TrainerProfile profile = TrainerProfile.builder()
                    .user(user)
                    .phone("9" + (100000000 + user.getId() * 111))
                    .location("Bangalore")
                    .bio(name + " is an experienced trainer specializing in " + String.join(" and ", subjects) + ".")
                    .yearsOfExperience(years)
                    .subjects(List.of(subjects))
                    .averageRating(rating)
                    .totalRatings(totalRatings)
                    .build();
            profile = trainerProfileRepository.save(profile);

            for (Object[] exp : expertise) {
                String skillName = (String) exp[0];
                TrainerExpertise.ExpertiseLevel level = TrainerExpertise.ExpertiseLevel.valueOf((String) exp[1]);
                trainerExpertiseRepository.save(TrainerExpertise.builder()
                        .trainerProfile(profile)
                        .skill(findSkill(skills, skillName))
                        .level(level)
                        .build());
            }

            for (int i = 0; i < certCount; i++) {
                trainerCertificationRepository.save(TrainerCertification.builder()
                        .trainerProfile(profile)
                        .name(subjects[0] + " Professional Certification " + (i + 1))
                        .issuingOrganization("Tech Certification Board")
                        .issueDate(java.time.LocalDate.now().minusYears(i + 1))
                        .credentialId("CERT-" + profile.getId() + "-" + (i + 1))
                        .build());
            }

            trainers.add(profile);
        }

        return trainers;
    }

    // ==================== TRAINEES ====================

    private List<TraineeProfile> seedTrainees(List<Skill> skills, List<Competency> competencies) {
        List<TraineeProfile> trainees = new ArrayList<>();
        Competency backendDev = competencies.get(0);

        Object[][] traineeData = {
                {"Karan Patel", "karan.patel@student.com",
                        new Object[][]{{"Java", 82.0}, {"SQL", 76.0}, {"Spring Boot", 42.0}, {"REST API", 31.0}, {"Git", 38.0}, {"Cloud Computing", 21.0}}},
                {"Neha Joshi", "neha.joshi@student.com",
                        new Object[][]{{"Java", 65.0}, {"SQL", 58.0}, {"Python", 70.0}, {"Data Analysis", 60.0}}},
                {"Sanjay Verma", "sanjay.verma@student.com",
                        new Object[][]{{"Cloud Computing", 35.0}, {"AWS", 28.0}, {"Docker", 40.0}, {"Git", 55.0}}},
                {"Ananya Iyer", "ananya.iyer@student.com",
                        new Object[][]{{"React", 68.0}, {"JavaScript", 72.0}, {"Git", 60.0}}},
                {"Rohan Mehta", "rohan.mehta@student.com",
                        new Object[][]{{"Cybersecurity", 25.0}, {"Cloud Computing", 30.0}, {"Communication", 55.0}}},
                {"Divya Nair", "divya.nair@student.com",
                        new Object[][]{{"Python", 45.0}, {"AI/ML", 33.0}, {"Data Analysis", 40.0}}},
                {"Aditya Rao", "aditya.rao@student.com",
                        new Object[][]{{"Java", 55.0}, {"Spring Boot", 30.0}, {"REST API", 25.0}, {"SQL", 48.0}, {"Git", 42.0}}},
                {"Pooja Desai", "pooja.desai@student.com",
                        new Object[][]{{"Docker", 38.0}, {"Kubernetes", 22.0}, {"Cloud Computing", 45.0}, {"Git", 50.0}}},
                {"Kabir Khan", "kabir.khan@student.com",
                        new Object[][]{{"Project Management", 60.0}, {"Communication", 68.0}, {"Git", 35.0}}},
                {"Ishita Bose", "ishita.bose@student.com",
                        new Object[][]{{"Java", 48.0}, {"SQL", 52.0}, {"Spring Boot", 20.0}, {"REST API", 18.0}, {"Git", 30.0}}}
        };

        for (Object[] data : traineeData) {
            String name = (String) data[0];
            String email = (String) data[1];
            Object[][] skillScores = (Object[][]) data[2];

            User user = userRepository.save(User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode("trainee123"))
                    .role(Role.TRAINEE)
                    .status(User.AccountStatus.ACTIVE)
                    .enabled(true)
                    .build());

            TraineeProfile profile = TraineeProfile.builder()
                    .user(user)
                    .phone("8" + (100000000 + user.getId() * 137))
                    .location("Bangalore")
                    .bio("Aspiring professional building skills through Capacity Connect.")
                    .interests(List.of("Java", "Cloud", "Data Science"))
                    .build();
            profile = traineeProfileRepository.save(profile);

            for (Object[] scoreData : skillScores) {
                String skillName = (String) scoreData[0];
                double score = (double) scoreData[1];

                TraineeSkill.ProficiencyLevel level = score >= 90 ? TraineeSkill.ProficiencyLevel.EXPERT
                        : score >= 70 ? TraineeSkill.ProficiencyLevel.ADVANCED
                        : score >= 40 ? TraineeSkill.ProficiencyLevel.INTERMEDIATE
                        : TraineeSkill.ProficiencyLevel.BEGINNER;

                traineeSkillRepository.save(TraineeSkill.builder()
                        .traineeProfile(profile)
                        .skill(findSkill(skills, skillName))
                        .proficiencyLevel(level)
                        .score(score)
                        .build());
            }

            if (name.equals("Karan Patel")) {
                traineeCompetencyRepository.save(TraineeCompetency.builder()
                        .traineeProfile(profile)
                        .competency(backendDev)
                        .overallScore(0.0)
                        .isTargetGoal(true)
                        .build());
            }

            trainees.add(profile);
        }

        return trainees;
    }

    // ==================== COURSES ====================

    private List<Course> seedCourses(List<TrainerProfile> trainers, List<Skill> skills, List<Competency> competencies) {
        List<Course> courses = new ArrayList<>();

        Object[][] courseData = {
                {0, "Spring Boot Fundamentals", "Backend Development", Course.Difficulty.BEGINNER, 8.0,
                        new Object[][]{{"Spring Boot", 30.0}}, "Backend Developer"},
                {0, "Advanced Java Programming", "Backend Development", Course.Difficulty.ADVANCED, 12.0,
                        new Object[][]{{"Java", 20.0}}, "Backend Developer"},
                {1, "Building REST APIs with Spring", "Backend Development", Course.Difficulty.INTERMEDIATE, 10.0,
                        new Object[][]{{"REST API", 35.0}, {"Spring Boot", 15.0}}, "Backend Developer"},
                {1, "Git & GitHub Essentials", "Tools", Course.Difficulty.BEGINNER, 4.0,
                        new Object[][]{{"Git", 40.0}}, "Backend Developer"},
                {2, "AWS Cloud Practitioner", "Cloud", Course.Difficulty.BEGINNER, 10.0,
                        new Object[][]{{"AWS", 35.0}, {"Cloud Computing", 25.0}}, "Cloud Engineer"},
                {2, "Docker & Kubernetes Bootcamp", "DevOps", Course.Difficulty.INTERMEDIATE, 14.0,
                        new Object[][]{{"Docker", 35.0}, {"Kubernetes", 30.0}}, "DevOps Engineer"},
                {3, "Python for Data Analysis", "Data Science", Course.Difficulty.BEGINNER, 9.0,
                        new Object[][]{{"Python", 30.0}, {"Data Analysis", 25.0}}, "Data Analyst"},
                {3, "Machine Learning Foundations", "Data Science", Course.Difficulty.INTERMEDIATE, 15.0,
                        new Object[][]{{"AI/ML", 35.0}, {"Python", 15.0}}, "AI/ML Engineer"},
                {4, "Cybersecurity Fundamentals", "Security", Course.Difficulty.BEGINNER, 8.0,
                        new Object[][]{{"Cybersecurity", 35.0}}, "Cybersecurity Analyst"},
                {4, "Cloud Security Essentials", "Security", Course.Difficulty.INTERMEDIATE, 7.0,
                        new Object[][]{{"Cloud Computing", 20.0}, {"Cybersecurity", 20.0}}, "Cybersecurity Analyst"}
        };

        for (Object[] data : courseData) {
            int trainerIndex = (int) data[0];
            String title = (String) data[1];
            String category = (String) data[2];
            Course.Difficulty difficulty = (Course.Difficulty) data[3];
            double hours = (double) data[4];
            Object[][] skillsCovered = (Object[][]) data[5];
            String competencyName = (String) data[6];

            Course course = Course.builder()
                    .title(title)
                    .description("Learn " + title + " through hands-on, practical training designed for real-world application.")
                    .category(category)
                    .difficulty(difficulty)
                    .durationHours(hours)
                    .trainer(trainers.get(trainerIndex))
                    .status(Course.CourseStatus.PUBLISHED)
                    .build();
            course = courseRepository.save(course);

            for (Object[] skillData : skillsCovered) {
                String skillName = (String) skillData[0];
                double improvement = (double) skillData[1];
                courseSkillRepository.save(CourseSkill.builder()
                        .course(course)
                        .skill(findSkill(skills, skillName))
                        .expectedImprovement(improvement)
                        .build());
            }

            if (competencyName != null) {
                Competency competency = competencies.stream()
                        .filter(c -> c.getName().equals(competencyName))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Seed competency not found: " + competencyName));
                courseCompetencyRepository.save(CourseCompetency.builder()
                        .course(course)
                        .competency(competency)
                        .build());
            }

            courses.add(course);
        }

        return courses;
    }

    // ==================== ENROLLMENTS & FEEDBACK ====================

    private void seedEnrollmentsAndFeedback(List<TraineeProfile> trainees, List<Course> courses) {
        int[][] enrollmentPlan = {
        // traineeIndex, courseIndex, progressPercent
                {0, 8, 100}, {0, 9, 40},
                {1, 6, 100}, {1, 7, 45},
                {2, 4, 80}, {2, 5, 20},
                {3, 0, 100}, {3, 1, 50},
                {4, 8, 70}, {4, 9, 100},
                {5, 6, 55}, {5, 7, 100},
                {6, 0, 40}, {6, 2, 100},
                {7, 5, 60}, {7, 4, 100},
                {8, 3, 100}, {8, 1, 25},
                {9, 2, 35}, {9, 3, 80}
        };

        double[] feedbackRatings = {5.0, 4.0, 4.5, 5.0, 3.5, 4.0, 4.5, 5.0};
        int feedbackIndex = 0;

        for (int[] plan : enrollmentPlan) {
            TraineeProfile trainee = trainees.get(plan[0]);
            Course course = courses.get(plan[1]);
            double progress = plan[2];

            Enrollment.EnrollmentStatus status = progress >= 100 ? Enrollment.EnrollmentStatus.COMPLETED
                    : progress > 0 ? Enrollment.EnrollmentStatus.IN_PROGRESS
                    : Enrollment.EnrollmentStatus.ENROLLED;

            Enrollment enrollment = Enrollment.builder()
                    .traineeProfile(trainee)
                    .course(course)
                    .status(status)
                    .progressPercent(progress)
                    .completedAt(status == Enrollment.EnrollmentStatus.COMPLETED
                            ? java.time.LocalDateTime.now().minusDays((long) (Math.random() * 30))
                            : null)
                    .build();
            enrollmentRepository.save(enrollment);

            if (status == Enrollment.EnrollmentStatus.COMPLETED && feedbackIndex < feedbackRatings.length) {
                double rating = feedbackRatings[feedbackIndex % feedbackRatings.length];
                feedbackIndex++;

                feedbackRepository.save(Feedback.builder()
                        .traineeProfile(trainee)
                        .course(course)
                        .rating(rating)
                        .comment("Great course, learned a lot and would recommend to others.")
                        .contentQuality(rating)
                        .trainerQuality(rating)
                        .difficultyRating(3.0)
                        .build());

                TrainerProfile trainer = course.getTrainer();
                List<Feedback> allFeedback = feedbackRepository.findByCourse_TrainerId(trainer.getId());
                double avg = allFeedback.stream().mapToDouble(Feedback::getRating).average().orElse(0.0);
                trainer.setAverageRating(Math.round(avg * 10.0) / 10.0);
                trainer.setTotalRatings(allFeedback.size());
                trainerProfileRepository.save(trainer);
            }
        }
    }

    // ==================== CERTIFICATES ====================

    private void seedCertificates(List<TraineeProfile> trainees, List<Skill> skills) {
        Object[][] certData = {
                {0, "Oracle Certified Java Programmer", "Oracle", new String[]{"Java"}},
                {0, "MySQL Database Fundamentals", "Oracle", new String[]{"SQL"}},
                {1, "Python for Data Science", "Coursera", new String[]{"Python"}},
                {2, "AWS Cloud Practitioner", "Amazon Web Services", new String[]{"AWS", "Cloud Computing"}},
                {3, "React Developer Certification", "Meta", new String[]{"React", "JavaScript"}},
                {4, "Certified Ethical Hacker Associate", "EC-Council", new String[]{"Cybersecurity"}},
                {5, "Machine Learning Specialization", "Coursera", new String[]{"AI/ML"}},
                {6, "Java SE 11 Developer", "Oracle", new String[]{"Java"}},
                {7, "Docker Certified Associate", "Docker Inc.", new String[]{"Docker"}},
                {8, "PMP Foundations", "PMI", new String[]{"Project Management"}}
        };

        for (Object[] data : certData) {
            int traineeIndex = (int) data[0];
            String name = (String) data[1];
            String org = (String) data[2];
            String[] relatedSkillNames = (String[]) data[3];

            List<Skill> relatedSkills = new ArrayList<>();
            for (String skillName : relatedSkillNames) {
                relatedSkills.add(findSkill(skills, skillName));
            }

            certificateRepository.save(Certificate.builder()
                    .traineeProfile(trainees.get(traineeIndex))
                    .name(name)
                    .issuingOrganization(org)
                    .issueDate(java.time.LocalDate.now().minusMonths((long) (Math.random() * 24) + 1))
                    .credentialId("CRED-" + (1000 + traineeIndex * 37))
                    .fileUrl("/uploads/certificates/demo-placeholder.pdf")
                    .relatedSkills(relatedSkills)
                    .build());
        }
    }

    // ==================== ASSESSMENTS ====================

    private List<Assessment> seedAssessments(List<TrainerProfile> trainers, List<Course> courses, List<Skill> skills) {
        List<Assessment> assessments = new ArrayList<>();

        assessments.add(buildRestApiAssessment(trainers.get(1), courses.get(2), skills));
        assessments.add(buildSpringBootAssessment(trainers.get(0), courses.get(0), skills));
        assessments.add(buildJavaAssessment(trainers.get(0), courses.get(1), skills));
        assessments.add(buildCloudAssessment(trainers.get(2), courses.get(4), skills));
        assessments.add(buildGitAssessment(trainers.get(1), courses.get(3), skills));

        return assessments;
    }

    private Assessment buildRestApiAssessment(TrainerProfile trainer, Course course, List<Skill> skills) {
        Assessment assessment = Assessment.builder()
                .title("REST API Fundamentals Assessment")
                .description("Test your understanding of RESTful API design and implementation")
                .course(course)
                .targetSkill(findSkill(skills, "REST API"))
                .createdBy(trainer)
                .totalMarks(5)
                .durationMinutes(15)
                .build();
        assessment = assessmentRepository.save(assessment);

        saveQuestion(assessment, "Which HTTP method is used to update an existing resource?",
                "GET", "POST", "PUT", "DELETE", "C");
        saveQuestion(assessment, "What does REST stand for?",
                "Representational State Transfer", "Remote State Transfer",
                "Representational Server Transfer", "Remote Server Transfer", "A");
        saveQuestion(assessment, "Which status code indicates a successful resource creation?",
                "200", "201", "204", "400", "B");
        saveQuestion(assessment, "Which HTTP method is idempotent and used to remove a resource?",
                "POST", "PUT", "PATCH", "DELETE", "D");
        saveQuestion(assessment, "In REST, resources are typically represented using which format?",
                "JSON/XML", "Binary only", "Plain text only", "CSV only", "A");

        return assessment;
    }

    private Assessment buildSpringBootAssessment(TrainerProfile trainer, Course course, List<Skill> skills) {
        Assessment assessment = Assessment.builder()
                .title("Spring Boot Basics Assessment")
                .description("Test your knowledge of Spring Boot fundamentals")
                .course(course)
                .targetSkill(findSkill(skills, "Spring Boot"))
                .createdBy(trainer)
                .totalMarks(5)
                .durationMinutes(15)
                .build();
        assessment = assessmentRepository.save(assessment);

        saveQuestion(assessment, "Which annotation marks a class as a Spring Boot application entry point?",
                "@SpringBootApplication", "@Controller", "@Service", "@Repository", "A");
        saveQuestion(assessment, "What is the default embedded server in Spring Boot?",
                "Jetty", "Tomcat", "Undertow", "GlassFish", "B");
        saveQuestion(assessment, "Which annotation is used to inject dependencies in Spring?",
                "@Inject", "@Wire", "@Autowired", "@Bean", "C");
        saveQuestion(assessment, "Which file is commonly used for Spring Boot configuration?",
                "config.xml", "application.properties", "settings.json", "boot.config", "B");
        saveQuestion(assessment, "Which annotation defines a REST controller in Spring Boot?",
                "@Controller", "@RestController", "@Service", "@Component", "B");

        return assessment;
    }

    private Assessment buildJavaAssessment(TrainerProfile trainer, Course course, List<Skill> skills) {
        Assessment assessment = Assessment.builder()
                .title("Core Java Assessment")
                .description("Test your core Java programming knowledge")
                .course(course)
                .targetSkill(findSkill(skills, "Java"))
                .createdBy(trainer)
                .totalMarks(5)
                .durationMinutes(15)
                .build();
        assessment = assessmentRepository.save(assessment);

        saveQuestion(assessment, "Which keyword is used to inherit a class in Java?",
                "implements", "extends", "inherits", "super", "B");
        saveQuestion(assessment, "Which collection does not allow duplicate elements?",
                "List", "Set", "Map", "Array", "B");
        saveQuestion(assessment, "What is the size of an int in Java?",
                "8 bits", "16 bits", "32 bits", "64 bits", "C");
        saveQuestion(assessment, "Which keyword is used to handle exceptions in Java?",
                "throw", "catch", "try", "All of the above", "D");
        saveQuestion(assessment, "Which method is the entry point of a Java application?",
                "start()", "run()", "main()", "init()", "C");

        return assessment;
    }

    private Assessment buildCloudAssessment(TrainerProfile trainer, Course course, List<Skill> skills) {
        Assessment assessment = Assessment.builder()
                .title("Cloud Computing Basics Assessment")
                .description("Test your understanding of cloud computing concepts")
                .course(course)
                .targetSkill(findSkill(skills, "Cloud Computing"))
                .createdBy(trainer)
                .totalMarks(5)
                .durationMinutes(15)
                .build();
        assessment = assessmentRepository.save(assessment);

        saveQuestion(assessment, "Which of these is a cloud service model for renting virtual machines?",
                "SaaS", "PaaS", "IaaS", "DaaS", "C");
        saveQuestion(assessment, "What does 'elasticity' mean in cloud computing?",
                "Fixed resource allocation", "Ability to scale resources on demand",
                "Manual server upgrades only", "Physical hardware flexibility", "B");
        saveQuestion(assessment, "Which AWS service provides scalable object storage?",
                "EC2", "S3", "RDS", "Lambda", "B");
        saveQuestion(assessment, "What is a key benefit of cloud computing?",
                "Higher upfront hardware cost", "Pay-as-you-go pricing", "No internet required", "Fixed capacity", "B");
        saveQuestion(assessment, "Which term describes running code without managing servers?",
                "Serverless computing", "Bare metal computing", "Grid computing", "Edge computing", "A");

        return assessment;
    }

    private Assessment buildGitAssessment(TrainerProfile trainer, Course course, List<Skill> skills) {
        Assessment assessment = Assessment.builder()
                .title("Git & Version Control Assessment")
                .description("Test your Git fundamentals")
                .course(course)
                .targetSkill(findSkill(skills, "Git"))
                .createdBy(trainer)
                .totalMarks(5)
                .durationMinutes(10)
                .build();
        assessment = assessmentRepository.save(assessment);

        saveQuestion(assessment, "Which command creates a new local Git repository?",
                "git start", "git init", "git create", "git new", "B");
        saveQuestion(assessment, "Which command stages changes for commit?",
                "git commit", "git push", "git add", "git stage", "C");
        saveQuestion(assessment, "Which command creates a new branch?",
                "git branch <name>", "git new <name>", "git create <name>", "git checkout -new <name>", "A");
        saveQuestion(assessment, "Which command uploads local commits to a remote repository?",
                "git pull", "git fetch", "git push", "git merge", "C");
        saveQuestion(assessment, "Which command shows the commit history?",
                "git history", "git log", "git commits", "git show-all", "B");

        return assessment;
    }

    private void saveQuestion(Assessment assessment, String text, String a, String b, String c, String d, String correct) {
        questionRepository.save(Question.builder()
                .assessment(assessment)
                .questionText(text)
                .optionA(a)
                .optionB(b)
                .optionC(c)
                .optionD(d)
                .correctOption(correct)
                .marks(1)
                .build());
    }

    // ==================== ACHIEVEMENTS ====================

    private void seedAchievements() {
        achievementRepository.save(Achievement.builder()
                .name("Java Mastery")
                .description("Reached 90%+ proficiency in Java")
                .icon("badge-java")
                .criteria(Achievement.AchievementCriteria.SKILL_MASTERY)
                .criteriaThreshold(90.0)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Assessment Champion")
                .description("Scored 90% or higher on an assessment")
                .icon("badge-assessment")
                .criteria(Achievement.AchievementCriteria.ASSESSMENT_HIGH_SCORE)
                .criteriaThreshold(90.0)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Course Completion")
                .description("Completed your first course")
                .icon("badge-course")
                .criteria(Achievement.AchievementCriteria.COURSE_COMPLETION_COUNT)
                .criteriaThreshold(1.0)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Dedicated Learner")
                .description("Completed 3 or more courses")
                .icon("badge-dedicated")
                .criteria(Achievement.AchievementCriteria.COURSE_COMPLETION_COUNT)
                .criteriaThreshold(3.0)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Skill Improver")
                .description("Improved a skill score by 30+ points through assessments")
                .icon("badge-improver")
                .criteria(Achievement.AchievementCriteria.SKILL_IMPROVEMENT)
                .criteriaThreshold(30.0)
                .build());
    }
}





/*int[][] enrollmentPlan = {
traineeIndex, courseIndex, progressPercent
       {0, 2, 100}, {0, 3, 60}, {0, 0, 30}, */