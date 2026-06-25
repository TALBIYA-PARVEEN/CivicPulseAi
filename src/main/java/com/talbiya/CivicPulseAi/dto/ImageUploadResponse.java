package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadResponse {

    private Long imageId;
    private String imageUrl;
}