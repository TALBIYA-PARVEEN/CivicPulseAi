package com.talbiya.CivicPulseAi.dto;

import lombok.Data;

@Data
public class AITriageResponse {

    private String category;
    private String severity;
    private String department;
}