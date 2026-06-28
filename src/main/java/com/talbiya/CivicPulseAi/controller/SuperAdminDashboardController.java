package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.dto.SuperAdminDashboardDTO;
import com.talbiya.CivicPulseAi.service.SuperAdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/super-admin/dashboard")
public class SuperAdminDashboardController {

    @Autowired
    private SuperAdminDashboardService dashboardService;

    // 🟢 SUMMARY CARDS (TOTAL, PENDING, RESOLVED, OVERDUE)
    @GetMapping("/summary")
    public SuperAdminDashboardDTO getSummary() {
        return dashboardService.getDashboardSummary();
    }

    // 📍 GIS MAP DATA
    @GetMapping("/map")
    public List<MapIssueDTO> getMapData() {
        return dashboardService.getAllMapIssues();
    }

    // 🔥 HEATMAP DATA
    @GetMapping("/heatmap")
    public Map<String, Long> getHeatmap() {
        return dashboardService.getAreaHeatmap();
    }
}