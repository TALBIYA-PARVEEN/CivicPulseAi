package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.enums.Priority;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueEscalationScheduler {

    @Autowired
    private IssueRepository issueRepository;

    // runs every 1 hour
    @Scheduled(fixedRate = 3600000)
    public void escalateOverdueIssues() {

        List<Issue> issues = issueRepository.findAll();

        for (Issue issue : issues) {

            if (issue.getDueDate() != null &&
                    issue.getResolvedAt() == null &&
                    issue.getDueDate().isBefore(LocalDateTime.now())) {

                issue.setPriority(Priority.CRITICAL);
            }
        }

        issueRepository.saveAll(issues);
    }
}
