package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GisService {

    private final IssueRepository issueRepository;

    public GisService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public List<MapIssueDTO> getMapIssues() {
        return issueRepository.fetchMapIssues();
    }

    // FIX: THIS METHOD WAS MISSING
    public Object getOverdueHeatmap() {
        // simple placeholder (no crash version)
        return issueRepository.findAll();
    }
}