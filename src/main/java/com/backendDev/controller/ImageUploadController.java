package com.backendDev.controller;

import com.backendDev.common.AppException;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.ImageUploadResponse;
import com.backendDev.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    private final FileStorageService fileStorageService;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024;

    @PostMapping(value = "/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadCategoryImage(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(uploadImage(file, "categories"));
    }

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadProductImage(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(uploadImage(file, "products"));
    }

    @DeleteMapping("/category/{filename}")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryImage(@PathVariable String filename) {
        fileStorageService.deleteFile("categories", filename);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully."));
    }

    @DeleteMapping("/product/{filename}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable String filename) {
        fileStorageService.deleteFile("products", filename);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully."));
    }

    private ApiResponse<ImageUploadResponse> uploadImage(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new AppException("No file provided.", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(
                    "Invalid file type. Allowed: JPG, PNG, WEBP, GIF.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new AppException("File size exceeds 5MB limit.", HttpStatus.BAD_REQUEST);
        }

        String filename      = fileStorageService.storeFile(file, subFolder);
        String imageUrl      = fileStorageService.buildImageUrl(subFolder, filename);
        String imagePath     = "/uploads/" + subFolder + "/" + filename;
        String originalName  = file.getOriginalFilename();

        log.info("Image uploaded: {} -> {}", originalName, imageUrl);

        ImageUploadResponse data = ImageUploadResponse.builder()
                .imageName(filename)
                .originalName(originalName)
                .imageType(contentType)
                .imagePath(imagePath)
                .imageUrl(imageUrl)
                .build();

        return ApiResponse.success("Image uploaded successfully.", data);
    }
}