package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuperAdminDashboardDTO {

    private long totalIssues;
    private long resolvedIssues;
    private long pendingIssues;
    private long overdueIssues;
}