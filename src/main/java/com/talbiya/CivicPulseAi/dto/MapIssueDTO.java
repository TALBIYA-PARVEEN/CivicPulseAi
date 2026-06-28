package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapIssueDTO {

    private Long id;
    private String category;
    private String status;
    private Double latitude;
    private Double longitude;
    private String aiSeverity;   // IMPORTANT: must match entity type
}