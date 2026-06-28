package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.AITriageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiServiceImpl implements GeminiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Override
    public String generateContent(String prompt) {

        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                }
              ]
            }
          ]
        }
        """.formatted(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity =
                new HttpEntity<>(requestBody, headers);

        String url = apiUrl + "?key=" + apiKey;

        return restTemplate.postForObject(
                url,
                entity,
                String.class
        );
    }

//    @Override
//    public AITriageResponse triageIssue(
//            String title,
//            String description) {
//
//        String prompt = """
//You are a civic issue classification AI.
//
//Analyze the issue and return ONLY valid JSON.
//
//{
//  "category":"ROAD/WATER/ELECTRICITY/SANITATION/OTHER",
//  "severity":"LOW/MEDIUM/HIGH",
//  "department":"Responsible Department"
//}
//
//Title:
//%s
//
//Description:
//%s
//""".formatted(title, description);
//
//        String response =
//                generateContent(prompt);
//
//        return parseResponse(response);
//    }


}