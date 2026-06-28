package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.entity.AdminRequest;
import com.talbiya.CivicPulseAi.entity.Issue;

import java.util.List;

public interface SuperAdminService {

    List<AdminRequest> getPendingRequests();

    void assignAdminToRequest(Long requestId, Long adminId);

    List<Issue> getOverdueIssues();

    List<Issue> getPendingIssues();

    List<Issue> getResolvedIssues();
}

