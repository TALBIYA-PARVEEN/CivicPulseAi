package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.CreateIssueRequest;
import com.talbiya.CivicPulseAi.dto.ImageUploadResponse;
import com.talbiya.CivicPulseAi.dto.IssueResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IssueService {

    IssueResponse createIssue(CreateIssueRequest request);

    List<IssueResponse> getAllIssues();

    IssueResponse getIssueById(Long id);

    ImageUploadResponse uploadImage(
            Long issueId,
            MultipartFile file);
}