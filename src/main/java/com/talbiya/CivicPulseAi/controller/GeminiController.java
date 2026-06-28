package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/test")
    public String testGemini() {

        return geminiService.generateContent(
                "Say Hello from Gemini"
        );
    }

//    @GetMapping("/triage")
//    public String triageTest() {
//
//        return geminiService.generateContent("""
//You are an AI civic issue classifier.
//
//Return ONLY valid JSON.
//
//Do not add markdown.
//Do not add explanations.
//Do not add extra fields.
//
//Use exactly this schema:
//
//{
//  "category":"ROAD",
//  "severity":"LOW|MEDIUM|HIGH",
//  "department":"department name"
//}
//
//Issue Title:
//Huge pothole near market
//
//Issue Description:
//Large pothole causing accidents and traffic congestion.
//""");
//    }

    @GetMapping("/triage")
    public String triageTest() {

        return geminiService.generateContent("""
You are an AI civic issue classifier.

Return only:
Category
Severity
Department

Issue Title:
Huge pothole near market

Issue Description:
Large pothole causing accidents and traffic congestion.
""");
    }

}