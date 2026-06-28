package com.talbiya.CivicPulseAi.dto;

import com.talbiya.CivicPulseAi.enums.IssueCategory;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class IssueResponse {

    private Long id;

    private String title;

    private String description;

    private Double latitude;

    private Double longitude;

    private IssueCategory category;

    private IssueStatus status;

    private LocalDateTime createdAt;

    private String reportedBy;

    private List<String> imageUrls;

    private String aiSeverity;

    private String aiDepartment;

    private Boolean isDuplicate;

    private Long masterIssueId;

    private Integer verificationCount;


}