package com.capacityconnect.controller;

import com.capacityconnect.dto.MessageResponse;
import com.capacityconnect.dto.notification.AnnouncementDto;
import com.capacityconnect.security.UserPrincipal;
import com.capacityconnect.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping("/api/admin/announcements")
    public ResponseEntity<AnnouncementDto> publish(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AnnouncementDto dto
    ) {
        return ResponseEntity.ok(announcementService.publish(principal.getId(), dto));
    }

    @DeleteMapping("/api/admin/announcements/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(MessageResponse.builder().message("Announcement deleted").success(true).build());
    }

    // Public-ish: any authenticated user (homepage/dashboard feed, section 22)
    @GetMapping("/api/public/announcements")
    public ResponseEntity<List<AnnouncementDto>> getAll() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/api/public/announcements/latest")
    public ResponseEntity<List<AnnouncementDto>> getLatest() {
        return ResponseEntity.ok(announcementService.getLatestAnnouncements());
    }
}