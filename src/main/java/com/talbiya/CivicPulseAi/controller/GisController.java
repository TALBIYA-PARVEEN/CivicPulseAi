package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.service.GisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gis")
public class GisController {

    private final GisService gisService;

    public GisController(GisService gisService) {
        this.gisService = gisService;
    }

    @GetMapping("/heatmap")
    public Object getHeatmap() {
        return gisService.getOverdueHeatmap();
    }
}