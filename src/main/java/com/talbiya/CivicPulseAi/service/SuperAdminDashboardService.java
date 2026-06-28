package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.dto.SuperAdminDashboardDTO;
import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SuperAdminDashboardService {

    @Autowired
    private IssueRepository issueRepository;

    // 🟢 MAIN DASHBOARD SUMMARY
    public SuperAdminDashboardDTO getDashboardSummary() {

        List<Issue> issues = issueRepository.findAll();

        long total = issues.size();

        long resolved = issues.stream()
                .filter(i -> i.getStatus() == IssueStatus.RESOLVED)
                .count();

        long pending = issues.stream()
                .filter(i -> i.getStatus() != IssueStatus.RESOLVED)
                .count();

        long overdue = issues.stream()
                .filter(i ->
                        i.getDueDate() != null &&
                                i.getResolvedAt() == null &&
                                i.getDueDate().isBefore(LocalDateTime.now())
                )
                .count();

        return new SuperAdminDashboardDTO(
                total,
                resolved,
                pending,
                overdue
        );
    }
    public List<MapIssueDTO> getAllMapIssues() {

        return issueRepository.findAll()
                .stream()
                .map(i -> new MapIssueDTO(
                        i.getId(),
                        i.getCategory().name(),
                        i.getStatus().name(),
                        i.getLatitude(),
                        i.getLongitude(),
                        i.getAiSeverity()
                ))
                .toList();
    }

    public Map<String, Long> getAreaHeatmap() {

        return issueRepository.findAll()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        i -> i.getCity() + " - " + i.getArea(),
                        java.util.stream.Collectors.counting()
                ));
    }
}

