package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.dto.SuperAdminDashboardDTO;
import com.talbiya.CivicPulseAi.entity.Issue;

import java.util.List;
import java.util.Map;

public interface SuperAdminDashboardService {

    // 📊 Main dashboard summary (total/resolved/pending/overdue)
    SuperAdminDashboardDTO getDashboardSummary();

    // 🗺️ GIS map data (all issues with coordinates)
    List<MapIssueDTO> getAllMapIssues();

    // 🔥 Heatmap by city + area
    Map<String, Long> getAreaHeatmap();

    // 🚨 Escalated / overdue issues
    List<Issue> getEscalatedIssues();

}