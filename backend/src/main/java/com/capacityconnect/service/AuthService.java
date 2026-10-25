package com.capacityconnect.service;

import com.capacityconnect.dto.auth.AuthResponse;
import com.capacityconnect.dto.auth.LoginRequest;
import com.capacityconnect.dto.auth.SignupRequest;
import com.capacityconnect.entity.Role;
import com.capacityconnect.entity.TraineeProfile;
import com.capacityconnect.entity.TrainerProfile;
import com.capacityconnect.entity.User;
import com.capacityconnect.exception.BadRequestException;
import com.capacityconnect.repository.TraineeProfileRepository;
import com.capacityconnect.repository.TrainerProfileRepository;
import com.capacityconnect.repository.UserRepository;
import com.capacityconnect.security.JwtUtil;
import com.capacityconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TraineeProfileRepository traineeProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(SignupRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists");
        }

        // Trainee accounts are auto-active; Trainer/Admin require admin approval
        User.AccountStatus status = (request.getRole() == Role.TRAINEE)
                ? User.AccountStatus.ACTIVE
                : User.AccountStatus.PENDING;

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(status)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Create the corresponding empty profile immediately so the trainee/trainer
        // dashboard has something to attach data to right after signup.
        if (request.getRole() == Role.TRAINEE) {
            TraineeProfile profile = TraineeProfile.builder()
                    .user(user)
                    .build();
            traineeProfileRepository.save(profile);
        } else if (request.getRole() == Role.TRAINER) {
            TrainerProfile profile = TrainerProfile.builder()
                    .user(user)
                    .build();
            trainerProfileRepository.save(profile);
        }

        // If PENDING, we still return a response (frontend needs to know what
        // happened) but we do NOT issue a usable token - status tells the
        // frontend to show an "awaiting approval" screen instead of logging in.
        String token = (status == User.AccountStatus.ACTIVE)
                ? jwtUtil.generateToken(new UserPrincipal(user), user.getId(), user.getRole().name())
                : null;

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(status.name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Check status BEFORE attempting authentication, so we can give a
        // specific "pending approval" message instead of a generic auth failure.
        if (user.getStatus() == User.AccountStatus.PENDING) {
            throw new DisabledException("Your account is pending admin approval");
        }
        if (user.getStatus() == User.AccountStatus.REJECTED) {
            throw new DisabledException("Your account registration was rejected. Contact admin for details.");
        }
        if (user.getStatus() == User.AccountStatus.SUSPENDED) {
            throw new DisabledException("Your account has been suspended. Contact admin for details.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtil.generateToken(principal, principal.getId(), principal.getRole());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus().name())
                .build();
    }
}