package com.talbiya.CivicPulseAi.repository;

import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("""
        SELECT new com.talbiya.CivicPulseAi.dto.MapIssueDTO(
            i.id,
            i.category,
            i.status,
            i.latitude,
            i.longitude,
            i.aiSeverity
        )
        FROM Issue i
    """)
    List<MapIssueDTO> fetchMapIssues();
    List<Issue> findByCityAndAreaAndAssignedAdminIsNull(String city, String area);
}