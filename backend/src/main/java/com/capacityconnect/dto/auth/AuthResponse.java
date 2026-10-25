package com.capacityconnect.dto.auth;

import com.capacityconnect.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned after successful login/signup - contains the JWT
 * and basic user info the frontend needs immediately (for routing to
 * the correct dashboard, and displaying the user's name).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;

    private Long userId;

    private String fullName;

    private String email;

    private Role role;

    private String status; // ACTIVE, PENDING, REJECTED, SUSPENDED
}