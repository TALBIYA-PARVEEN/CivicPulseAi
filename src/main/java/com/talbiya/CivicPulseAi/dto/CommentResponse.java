package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {

    private Long id;

    private String commentText;

    private String userEmail;

    private LocalDateTime createdAt;
}