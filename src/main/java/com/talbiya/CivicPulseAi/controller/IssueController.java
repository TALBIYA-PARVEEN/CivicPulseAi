package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.CreateIssueRequest;
import com.talbiya.CivicPulseAi.dto.ImageUploadResponse;
import com.talbiya.CivicPulseAi.dto.IssueResponse;
import com.talbiya.CivicPulseAi.dto.UpdateIssueStatusRequest;
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
}