package com.talbiya.CivicPulseAi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.talbiya.CivicPulseAi.dto.*;
import com.talbiya.CivicPulseAi.entity.*;
import com.talbiya.CivicPulseAi.enums.*;
import com.talbiya.CivicPulseAi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRequestRepository adminRequestRepository;

    @Autowired
    private AiTriageService aiTriageService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private IssueImageRepository issueImageRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private CommentRepository commentRepository;

    // =========================
    // CREATE ISSUE (CORE FLOW)
    // =========================
    @Override
    public IssueResponse createIssue(CreateIssueRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setCity(request.getCity());
        issue.setArea(request.getArea());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setCategory(request.getCategory());
        issue.setStatus(IssueStatus.REPORTED);
        issue.setReportedBy(user);

        // =========================
        // SLA ENGINE (IMPORTANT)
        // =========================
        LocalDateTime dueDate = LocalDateTime.now();

        switch (request.getCategory()) {
            case WATER -> dueDate = dueDate.plusDays(3);
            case ROAD -> dueDate = dueDate.plusDays(7);
            case ELECTRICITY -> dueDate = dueDate.plusDays(2);
            default -> dueDate = dueDate.plusDays(5);
        }

        issue.setDueDate(dueDate);

        // =========================
        // PRIORITY ENGINE
        // =========================
        switch (request.getCategory()) {
            case ELECTRICITY, WATER -> issue.setPriority(Priority.HIGH);
            case ROAD -> issue.setPriority(Priority.MEDIUM);
            default -> issue.setPriority(Priority.LOW);
        }

        // =========================
        // AI TRIAGE
        // =========================
        try {
            Map<String, String> ai = aiTriageService.analyzeIssue(
                    request.getTitle(),
                    request.getDescription()
            );

            issue.setAiSeverity(ai.get("severity"));
            issue.setAiDepartment(ai.get("department"));

        } catch (Exception e) {
            issue.setAiSeverity("UNKNOWN");
            issue.setAiDepartment("OTHER");
        }

        // =========================
        // ADMIN ASSIGNMENT LOGIC
        // =========================
        Optional<User> adminOpt =
                userRepository.findByRoleAndCityAndArea(
                        Role.ADMIN,
                        request.getCity(),
                        request.getArea()
                );

        if (adminOpt.isPresent()) {

            issue.setAssignedAdmin(adminOpt.get());

            alertService.sendUserAlert(
                    adminOpt.get(),
                    "New Issue Assigned: " + issue.getTitle()
            );

        } else {

            issue.setAssignedAdmin(null);

            AdminRequest req = adminRequestRepository
                    .findByCityAndArea(request.getCity(), request.getArea())
                    .orElse(null);

            if (req == null) {
                req = new AdminRequest();
                req.setCity(request.getCity());
                req.setArea(request.getArea());
                req.setIssueCount(1);
                req.setStatus(RequestStatus.PENDING);
                req.setCreatedAt(LocalDateTime.now());
            } else {
                req.setIssueCount(req.getIssueCount() + 1);
                req.setUpdatedAt(LocalDateTime.now());
            }

            adminRequestRepository.save(req);

            alertService.sendGlobalAlert(
                    "New Admin Request: " +
                            request.getCity() + " - " + request.getArea()
            );
        }

        // =========================
        // SAVE ISSUE
        // =========================
        Issue saved = issueRepository.save(issue);

        alertService.sendUserAlert(
                user,
                "Issue created successfully: " + saved.getTitle()
        );

        return mapToResponse(saved);
    }

    // =========================
    // STATUS UPDATE
    // =========================
    @Override
    public IssueResponse updateIssueStatus(Long issueId, UpdateIssueStatusRequest request) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setStatus(request.getStatus());

        if (request.getStatus() == IssueStatus.RESOLVED) {
            issue.setResolvedAt(LocalDateTime.now());
        }

        Issue updated = issueRepository.save(issue);

        alertService.sendUserAlert(
                updated.getReportedBy(),
                "Issue status updated: " + updated.getStatus()
        );

        return mapToResponse(updated);
    }

    // =========================
    // IMAGE UPLOAD
    // =========================
    @Override
    public ImageUploadResponse uploadImage(Long issueId, MultipartFile file) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        try {
            Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            IssueImage img = new IssueImage();
            img.setIssue(issue);
            img.setImageUrl(upload.get("secure_url").toString());

            IssueImage saved = issueImageRepository.save(img);

            return new ImageUploadResponse(saved.getId(), saved.getImageUrl());

        } catch (Exception e) {
            throw new RuntimeException("Upload failed");
        }
    }

    // =========================
    // VERIFY ISSUE
    // =========================
    @Override
    public VerificationResponse verifyIssue(Long issueId) {

        User user = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow();

        Issue issue = issueRepository.findById(issueId).orElseThrow();

        if (verificationRepository.existsByIssueAndUser(issue, user)) {
            throw new RuntimeException("Already verified");
        }

        Verification v = new Verification();
        v.setIssue(issue);
        v.setUser(user);
        v.setVerifiedAt(LocalDateTime.now());

        verificationRepository.save(v);

        return new VerificationResponse(issueId,
                (int) verificationRepository.countByIssue(issue));
    }

    // =========================
    // COMMENTS
    // =========================
    @Override
    public CommentResponse addComment(Long issueId, CreateCommentRequest request) {

        User user = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow();

        Issue issue = issueRepository.findById(issueId).orElseThrow();

        Comment c = new Comment();
        c.setCommentText(request.getCommentText());
        c.setUser(user);
        c.setIssue(issue);
        c.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(c);

        return new CommentResponse(
                saved.getId(),
                saved.getCommentText(),
                saved.getUser().getEmail(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<CommentResponse> getCommentsByIssue(Long issueId) {

        Issue issue = issueRepository.findById(issueId).orElseThrow();

        return commentRepository.findByIssue(issue)
                .stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getCommentText(),
                        c.getUser().getEmail(),
                        c.getCreatedAt()
                ))
                .toList();
    }

    // =========================
    // MAPPER
    // =========================
    private IssueResponse mapToResponse(Issue issue) {

        List<String> images = issue.getImages()
                .stream()
                .map(IssueImage::getImageUrl)
                .toList();

        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getLatitude(),
                issue.getLongitude(),
                issue.getCategory(),
                issue.getStatus(),
                issue.getCreatedAt(),
                issue.getReportedBy().getEmail(),
                images,
                issue.getAiSeverity(),
                issue.getAiDepartment(),
                issue.getIsDuplicate(),
                issue.getMasterIssueId(),
                (int) verificationRepository.countByIssue(issue)
        );
    }

    @Override
    public List<IssueResponse> getAllIssues() {

        return issueRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public IssueResponse getIssueById(Long id) {

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        return mapToResponse(issue);
    }
}