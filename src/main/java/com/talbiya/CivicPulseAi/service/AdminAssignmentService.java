package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAssignmentService {

    @Autowired
    private IssueRepository issueRepository;

    public void assignPendingIssues(String city, String area, User admin) {

        List<Issue> issues =
                issueRepository.findByCityAndAreaAndAssignedAdminIsNull(city, area);

        for (Issue i : issues) {
            i.setAssignedAdmin(admin);
        }

        issueRepository.saveAll(issues);
    }
}