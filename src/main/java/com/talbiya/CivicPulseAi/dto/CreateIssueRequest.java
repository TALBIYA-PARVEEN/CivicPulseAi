package com.talbiya.CivicPulseAi.dto;

import com.talbiya.CivicPulseAi.enums.IssueCategory;
import lombok.Data;

@Data
public class CreateIssueRequest {

    private String title;

    private String description;

    private Double latitude;

    private Double longitude;

    private IssueCategory category;

    private String city;

    private String area;
}