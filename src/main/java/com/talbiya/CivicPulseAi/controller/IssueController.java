package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.*;
import com.talbiya.CivicPulseAi.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping
    public IssueResponse createIssue(
            @RequestBody CreateIssueRequest request) {

        return issueService.createIssue(request);
    }

    @GetMapping
    public List<IssueResponse> getAllIssues() {

        return issueService.getAllIssues();
    }

    @GetMapping("/{id}")
    public IssueResponse getIssueById(
            @PathVariable Long id) {

        return issueService.getIssueById(id);
    }

    @PostMapping("/{issueId}/images")
    public ImageUploadResponse uploadImage(
            @PathVariable Long issueId,
            @RequestParam("file") MultipartFile file) {

        return issueService.uploadImage(issueId, file);
    }

    @PutMapping("/{issueId}/status")
    public IssueResponse updateStatus(
            @PathVariable Long issueId,
            @RequestBody UpdateIssueStatusRequest request) {

        return issueService
                .updateIssueStatus(issueId, request);
    }

    @PostMapping("/{issueId}/verify")
    public VerificationResponse verifyIssue(
            @PathVariable Long issueId) {

        return issueService.verifyIssue(issueId);
    }


    @PostMapping("/{issueId}/comments")
    public CommentResponse addComment(
            @PathVariable Long issueId,
            @RequestBody CreateCommentRequest request) {

        return issueService.addComment(
                issueId,
                request
        );
    }

    @GetMapping("/{issueId}/comments")
    public List<CommentResponse> getComments(
            @PathVariable Long issueId) {

        return issueService.getCommentsByIssue(issueId);
    }
}