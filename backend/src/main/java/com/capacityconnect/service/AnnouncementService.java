package com.capacityconnect.service;

import com.capacityconnect.dto.notification.AnnouncementDto;
import com.capacityconnect.entity.Announcement;
import com.capacityconnect.entity.User;
import com.capacityconnect.exception.ResourceNotFoundException;
import com.capacityconnect.repository.AnnouncementRepository;
import com.capacityconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnnouncementDto publish(Long adminUserId, AnnouncementDto dto) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType() != null ? dto.getType() : Announcement.AnnouncementType.GENERAL)
                .publishedBy(admin)
                .build();
        announcement = announcementRepository.save(announcement);
        return toDto(announcement);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getLatestAnnouncements() {
        return announcementRepository.findTop5ByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement not found");
        }
        announcementRepository.deleteById(id);
    }

    private AnnouncementDto toDto(Announcement a) {
        return AnnouncementDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .type(a.getType())
                .publishedByName(a.getPublishedBy().getFullName())
                .createdAt(a.getCreatedAt())
                .build();
    }
}