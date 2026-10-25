package com.capacityconnect.dto.admin;

import com.capacityconnect.entity.Role;
import com.capacityconnect.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private User.AccountStatus status;
    private LocalDateTime createdAt;
}