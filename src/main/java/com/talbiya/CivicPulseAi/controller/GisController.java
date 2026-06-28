package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.service.GisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gis")
public class GisController {

    @Autowired
    private GisService gisService;

    @GetMapping("/map-data")
    public List<MapIssueDTO> getMapData() {
        return gisService.getAllIssuesForMap();
    }

    @GetMapping("/heatmap")
    public Map<String, Long> getHeatmap() {
        return gisService.getIssueHeatByArea();
    }

    @GetMapping("/overdue-heatmap")
    public Map<String, Long> getOverdueHeatmap() {
        return gisService.getOverdueHeatmap();
    }
}