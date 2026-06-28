package com.talbiya.CivicPulseAi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiTriageService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> analyzeIssue(String title, String description) {

        String prompt = """
                You are an AI assistant for a civic complaint system.

                Analyze the issue and return ONLY JSON in this format:
                {
                  "severity": "LOW | MEDIUM | HIGH | CRITICAL",
                  "department": "ELECTRICITY | WATER | GARBAGE | SEWAGE | ROAD | OTHER"
                }

                Rules:
                - Severity depends on urgency and impact
                - Department depends on civic domain
                - Return ONLY valid JSON, no explanation

                Issue Title: %s
                Issue Description: %s
                """.formatted(title, description);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        Map response = restTemplate.postForObject(url, requestBody, Map.class);

        System.out.println("GEMINI RESPONSE:");
        System.out.println(response);

        try {
            Map candidate =
                    (Map) ((java.util.List) response.get("candidates"))
                            .get(0);

            Map content =
                    (Map) candidate.get("content");

            Map part =
                    (Map) ((java.util.List) content.get("parts"))
                            .get(0);

            String text =
                    (String) part.get("text");

            System.out.println("EXTRACTED TEXT:");
            System.out.println(text);

            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();

            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(text, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("AI parsing failed", e);
        }
    }
}