package com.talbiya.CivicPulseAi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DuplicateDetectionAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isDuplicate(
            String newTitle,
            String newDescription,
            String existingTitle,
            String existingDescription
    ) {

        String prompt = """
You are a STRICT civic duplicate detection system.

Two issues are duplicate ONLY if they describe EXACT SAME real-world incident.

Return JSON ONLY:
{
  "duplicate": true
}
OR
{
  "duplicate": false
}

Rules:
- Same location + same problem type = likely duplicate
- Different problem type = NOT duplicate
- Be strict, avoid guessing

Issue A:
Title: %s
Description: %s

Issue B:
Title: %s
Description: %s
""".formatted(newTitle, newDescription, existingTitle, existingDescription);

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts",
                                new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        Map response =
                restTemplate.postForObject(
                        url,
                        requestBody,
                        Map.class
                );

        try {

            Map candidate =
                    (Map) ((java.util.List) response.get("candidates")).get(0);

            Map content =
                    (Map) candidate.get("content");

            java.util.List parts =
                    (java.util.List) content.get("parts");

            // SAFE text extraction
            String text = parts.get(0).toString();

            // Extract actual model output
            if (text.contains("text=")) {
                text = text.substring(text.indexOf("text=") + 5);
            }

            // Remove markdown artifacts
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Extract JSON block
            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");

            if (start == -1 || end == -1) {
                throw new RuntimeException("Invalid Gemini response: " + text);
            }

            String json = text.substring(start, end + 1);

            Map<String, Object> result =
                    new ObjectMapper().readValue(json, Map.class);

            return Boolean.parseBoolean(result.get("duplicate").toString());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}