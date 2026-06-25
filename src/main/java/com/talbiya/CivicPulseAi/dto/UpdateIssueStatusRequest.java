package com.talbiya.CivicPulseAi.dto;

import com.talbiya.CivicPulseAi.enums.IssueStatus;
import lombok.Data;

@Data
public class UpdateIssueStatusRequest {

    private IssueStatus status;
}
