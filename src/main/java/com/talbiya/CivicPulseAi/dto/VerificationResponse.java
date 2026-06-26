package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationResponse {

    private Long issueId;
    private int verificationCount;
}
