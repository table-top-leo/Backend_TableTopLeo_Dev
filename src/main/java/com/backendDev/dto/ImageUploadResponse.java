package com.backendDev.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadResponse {

    private String imageName;
    private String originalName;
    private String imageType;
    private String imagePath;
    private String imageUrl;
}