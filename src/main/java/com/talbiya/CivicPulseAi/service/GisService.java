package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.repository.IssueRepository;
import com.talbiya.CivicPulseAi.entity.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GisService {

    @Autowired
    private IssueRepository issueRepository;

    public List<MapIssueDTO> getAllIssuesForMap() {

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

    public Map<String, Long> getIssueHeatByArea() {

        return issueRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCity() + " - " + i.getArea(),
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getOverdueHeatmap() {

        return issueRepository.findAll()
                .stream()
                .filter(i ->
                        i.getDueDate() != null &&
                                i.getResolvedAt() == null &&
                                i.getDueDate().isBefore(LocalDateTime.now())
                )
                .collect(Collectors.groupingBy(
                        i -> i.getCity() + " - " + i.getArea(),
                        Collectors.counting()
                ));
    }
}

