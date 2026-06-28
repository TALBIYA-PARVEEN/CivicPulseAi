package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.entity.AdminRequest;
import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import com.talbiya.CivicPulseAi.enums.RequestStatus;
import com.talbiya.CivicPulseAi.repository.AdminRequestRepository;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import com.talbiya.CivicPulseAi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private AdminRequestRepository adminRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminAssignmentService adminAssignmentService;

    // ✅ 1. GET PENDING REQUESTS
    @Override
    public List<AdminRequest> getPendingRequests() {
        return adminRequestRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .toList();
    }

    // ✅ 2. ASSIGN ADMIN TO REQUEST
    @Override
    public void assignAdminToRequest(Long requestId, Long adminId) {

        AdminRequest req = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        req.setStatus(RequestStatus.ASSIGNED);
        req.setAssignedAdmin(admin);
        req.setUpdatedAt(LocalDateTime.now());

        adminRequestRepository.save(req);

        adminAssignmentService.assignPendingIssues(
                req.getCity(),
                req.getArea(),
                admin
        );
    }

    @Override
    public List<Issue> getOverdueIssues() {
        return issueRepository.findAll()
                .stream()
                .filter(i ->
                        i.getDueDate() != null &&
                                i.getResolvedAt() == null &&
                                i.getDueDate().isBefore(LocalDateTime.now())
                )
                .toList();
    }

    @Override
    public List<Issue> getPendingIssues() {
        return issueRepository.findAll()
                .stream()
                .filter(i ->
                        i.getStatus() == IssueStatus.REPORTED ||
                                i.getStatus() == IssueStatus.IN_PROGRESS
                )
                .toList();
    }

    @Override
    public List<Issue> getResolvedIssues() {
        return issueRepository.findAll()
                .stream()
                .filter(i ->
                        i.getStatus() == IssueStatus.RESOLVED
                )
                .toList();
    }
}
