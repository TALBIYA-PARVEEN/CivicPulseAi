package com.talbiya.CivicPulseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssueStatusUpdateMessage {

    private Long issueId;

    private String status;
}