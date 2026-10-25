package com.capacityconnect.dto.profile;

import lombok.Data;

import java.util.List;

@Data
public class UpdateTrainerProfileRequest {
    private String phone;
    private String location;
    private String bio;
    private Integer yearsOfExperience;
    private List<String> subjects;
}