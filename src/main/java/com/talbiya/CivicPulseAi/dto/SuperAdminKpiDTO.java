package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class SuperAdminKpiDTO {

    private long totalIssues;
    private long resolvedIssues;
    private long overdueIssues;
    private long pendingIssues;

    public SuperAdminKpiDTO(long totalIssues, long resolvedIssues, long overdueIssues, long pendingIssues) {
        this.totalIssues = totalIssues;
        this.resolvedIssues = resolvedIssues;
        this.overdueIssues = overdueIssues;
        this.pendingIssues = pendingIssues;
    }
}