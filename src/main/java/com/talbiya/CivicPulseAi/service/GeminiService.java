package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.AITriageResponse;

public interface GeminiService {

    String generateContent(String prompt);

//    AITriageResponse triageIssue(
//            String title,
//            String description);
}