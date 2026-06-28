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
import java.util.stream.Collectors;

@Service
public class SuperAdminDashboardServiceImpl implements SuperAdminDashboardService {

    @Autowired
    private IssueRepository issueRepository;

    // =========================
    // ✅ INTERFACE METHODS ONLY
    // =========================

    @Override
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
                .filter(i -> i.getDueDate() != null
                        && i.getResolvedAt() == null
                        && i.getDueDate().isBefore(LocalDateTime.now()))
                .count();

        return new SuperAdminDashboardDTO(total, resolved, pending, overdue);
    }

    @Override
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

    @Override
    public Map<String, Long> getAreaHeatmap() {

        return issueRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCity() + " - " + i.getArea(),
                        Collectors.counting()
                ));
    }

    @Override
    public List<Issue> getEscalatedIssues() {

        return issueRepository.findAll()
                .stream()
                .filter(i -> i.getDueDate() != null
                        && i.getResolvedAt() == null
                        && i.getDueDate().isBefore(LocalDateTime.now()))
                .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
                .toList();
    }

    // =========================
    // 🚀 EXTRA ANALYTICS (NOT IN INTERFACE)
    // =========================

    public Map<String, Object> getKpiMetrics() {

        List<Issue> issues = issueRepository.findAll();

        long total = issues.size();

        long resolved = issues.stream()
                .filter(i -> i.getStatus() == IssueStatus.RESOLVED)
                .count();

        long overdue = issues.stream()
                .filter(i -> i.getDueDate() != null
                        && i.getResolvedAt() == null
                        && i.getDueDate().isBefore(LocalDateTime.now()))
                .count();

        double resolutionRate = total == 0 ? 0 :
                (resolved * 100.0) / total;

        double slaCompliance = total == 0 ? 0 :
                ((total - overdue) * 100.0) / total;

        return Map.of(
                "totalIssues", total,
                "resolvedIssues", resolved,
                "overdueIssues", overdue,
                "resolutionRate", resolutionRate,
                "slaCompliance", slaCompliance
        );
    }

    public Map<String, Double> getCityPerformanceScore() {

        return issueRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Issue::getCity,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long total = list.size();
                                    long resolved = list.stream()
                                            .filter(i -> i.getStatus() == IssueStatus.RESOLVED)
                                            .count();

                                    return total == 0 ? 0.0 :
                                            (resolved * 100.0) / total;
                                }
                        )
                ));
    }

    public Map<String, Double> getAdminEfficiency() {

        return issueRepository.findAll()
                .stream()
                .filter(i -> i.getAssignedAdmin() != null)
                .collect(Collectors.groupingBy(
                        i -> i.getAssignedAdmin().getEmail(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long total = list.size();
                                    long resolved = list.stream()
                                            .filter(i -> i.getStatus() == IssueStatus.RESOLVED)
                                            .count();

                                    return total == 0 ? 0.0 :
                                            (resolved * 100.0) / total;
                                }
                        )
                ));
    }

    public Map<String, Long> getHotZones() {

        return issueRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCity() + " - " + i.getArea(),
                        Collectors.counting()
                ));
    }
}