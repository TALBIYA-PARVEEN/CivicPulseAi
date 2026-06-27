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
}