package com.capacityconnect.service;

import com.capacityconnect.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Handles saving uploaded files (certificates, resources, thumbnails, etc.)
 * to local disk under app.upload.dir, and returns a relative URL the
 * frontend can use directly (served statically - see WebConfig).
 */
@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "pdf", "jpg", "jpeg", "png", "mp4", "ppt", "pptx", "doc", "docx"
    );

    public String store(MultipartFile file, String subfolder) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("File type not allowed: ." + extension);
        }

        String uniqueFilename = UUID.randomUUID() + "." + extension;

        try {
            Path targetDir = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Relative URL served by WebConfig's static resource handler
            return "/uploads/" + subfolder + "/" + uniqueFilename;

        } catch (IOException ex) {
            throw new BadRequestException("Failed to store file: " + ex.getMessage());
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            throw new BadRequestException("File must have a valid extension");
        }
        return filename.substring(lastDot + 1);
    }
}