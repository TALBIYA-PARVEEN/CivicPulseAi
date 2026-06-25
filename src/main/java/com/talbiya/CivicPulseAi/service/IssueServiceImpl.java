package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.CreateIssueRequest;
import com.talbiya.CivicPulseAi.dto.IssueResponse;
import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public IssueResponse createIssue(CreateIssueRequest request) {

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setCategory(request.getCategory());

        issue.setStatus(IssueStatus.REPORTED);
        issue.setCreatedAt(LocalDateTime.now());

        Issue savedIssue = issueRepository.save(issue);

        return mapToResponse(savedIssue);
    }

    @Override
    public List<IssueResponse> getAllIssues() {

        return issueRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public IssueResponse getIssueById(Long id) {

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        return mapToResponse(issue);
    }

    private IssueResponse mapToResponse(Issue issue) {

        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getLatitude(),
                issue.getLongitude(),
                issue.getCategory(),
                issue.getStatus(),
                issue.getCreatedAt()
        );
    }
}