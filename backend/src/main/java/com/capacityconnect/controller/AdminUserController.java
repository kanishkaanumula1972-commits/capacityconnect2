package com.capacityconnect.controller;

import com.capacityconnect.dto.admin.AdminDashboardDto;
import com.capacityconnect.dto.admin.AdminUserDto;
import com.capacityconnect.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        return ResponseEntity.ok(adminUserService.getDashboardSummary());
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @GetMapping("/users/pending")
    public ResponseEntity<List<AdminUserDto>> getPendingUsers() {
        return ResponseEntity.ok(adminUserService.getPendingUsers());
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<AdminUserDto> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.approveUser(id));
    }

    @PutMapping("/users/{id}/reject")
    public ResponseEntity<AdminUserDto> rejectUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.rejectUser(id));
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<AdminUserDto> suspendUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.suspendUser(id));
    }

    @PutMapping("/users/{id}/reactivate")
    public ResponseEntity<AdminUserDto> reactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.reactivateUser(id));
    }
}