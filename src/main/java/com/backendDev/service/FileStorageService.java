package com.backendDev.service;

import com.backendDev.common.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    public String storeFile(MultipartFile file, String subFolder) {
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        );

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;

        try {
            Path uploadPath = Paths.get(uploadDir, subFolder).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored: {}/{}", subFolder, uniqueFilename);
            return uniqueFilename;

        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFilename, ex);
            throw new AppException("Failed to store image. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteFile(String subFolder, String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            Path filePath = Paths.get(uploadDir, subFolder, filename).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}/{}", subFolder, filename);
        } catch (IOException ex) {
            log.warn("Could not delete file: {}/{}", subFolder, filename);
        }
    }

    public String buildImageUrl(String subFolder, String filename) {
        if (filename == null || filename.isBlank()) return null;
        return baseUrl + "/uploads/" + subFolder + "/" + filename;
    }

    public String extractFilename(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        int lastSlash = imageUrl.lastIndexOf("/");
        if (lastSlash >= 0 && lastSlash < imageUrl.length() - 1) {
            return imageUrl.substring(lastSlash + 1);
        }
        return null;
    }
}