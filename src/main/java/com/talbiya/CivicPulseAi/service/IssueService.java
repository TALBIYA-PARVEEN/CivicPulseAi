package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IssueService {

    IssueResponse createIssue(CreateIssueRequest request);

    List<IssueResponse> getAllIssues();

    IssueResponse getIssueById(Long id);

    ImageUploadResponse uploadImage(
            Long issueId,
            MultipartFile file);

    IssueResponse updateIssueStatus(
            Long issueId,
            UpdateIssueStatusRequest request);

    VerificationResponse verifyIssue(Long issueId);


    CommentResponse addComment(
            Long issueId,
            CreateCommentRequest request);
    List<CommentResponse> getCommentsByIssue(
            Long issueId);
}