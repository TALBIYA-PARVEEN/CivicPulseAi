package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapIssueDTO {

    private Long id;
    private String category;
    private String status;
    private Double latitude;
    private Double longitude;
    private String severity;
}