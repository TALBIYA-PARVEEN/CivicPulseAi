package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.service.DuplicateDetectionAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/duplicate-test")
public class DuplicateTestController {

    @Autowired
    private DuplicateDetectionAiService service;

    @GetMapping
    public Boolean test() {

        return service.isDuplicate(
                "No electricity in sector 5",
                "Power outage affecting homes",

                "Electricity failure in sector 5",
                "Residents are facing power cuts"
        );
    }
}