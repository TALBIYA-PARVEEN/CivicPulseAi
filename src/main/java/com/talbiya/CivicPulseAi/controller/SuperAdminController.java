package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.dto.SuperAdminDashboardDTO;
import com.talbiya.CivicPulseAi.entity.AdminRequest;
import com.talbiya.CivicPulseAi.service.SuperAdminDashboardServiceImpl;
import com.talbiya.CivicPulseAi.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/super-admin")
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;

    @Autowired
    private SuperAdminDashboardServiceImpl dashboardService;

    // =========================
    // ADMIN REQUESTS
    // =========================

    @GetMapping("/requests")
    public List<AdminRequest> getRequests() {
        return superAdminService.getPendingRequests();
    }

    @PostMapping("/assign")
    public String assignAdmin(@RequestParam Long requestId,
                              @RequestParam Long adminId) {

        superAdminService.assignAdminToRequest(requestId, adminId);
        return "Admin assigned successfully";
    }

    // =========================
    // DASHBOARD APIs (IMPORTANT)
    // =========================

    @GetMapping("/summary")
    public SuperAdminDashboardDTO getSummary() {
        return dashboardService.getDashboardSummary();
    }

    @GetMapping("/map")
    public List<MapIssueDTO> getMap() {
        return dashboardService.getAllMapIssues();
    }

    @GetMapping("/heatmap")
    public Map<String, Long> getHeatmap() {
        return dashboardService.getAreaHeatmap();
    }
}