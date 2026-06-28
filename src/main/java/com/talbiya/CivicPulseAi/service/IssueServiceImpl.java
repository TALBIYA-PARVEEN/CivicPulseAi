package com.talbiya.CivicPulseAi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.talbiya.CivicPulseAi.dto.*;
import com.talbiya.CivicPulseAi.entity.*;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import com.talbiya.CivicPulseAi.enums.RequestStatus;
import com.talbiya.CivicPulseAi.enums.Role;
import com.talbiya.CivicPulseAi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import com.talbiya.CivicPulseAi.entity.AdminRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private AdminRequestRepository adminRequestRepository;


    @Autowired
    private DuplicateDetectionAiService duplicateDetectionAiService;

    @Autowired
    private DuplicateDetectionService duplicateDetectionService;

    @Autowired
    private AiTriageService aiTriageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private IssueImageRepository issueImageRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public IssueResponse createIssue(CreateIssueRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setCategory(request.getCategory());
        issue.setCity(request.getCity());
        issue.setArea(request.getArea());

        issue.setStatus(IssueStatus.REPORTED);
        issue.setCreatedAt(LocalDateTime.now());
        issue.setReportedBy(user);

        LocalDateTime dueDate = issue.getCreatedAt();

        switch (request.getCategory()) {
            case WATER -> dueDate = dueDate.plusDays(3);
            case ROAD -> dueDate = dueDate.plusDays(7);
            case ELECTRICITY -> dueDate = dueDate.plusDays(2);
            default -> dueDate = dueDate.plusDays(5);
        }

        issue.setDueDate(dueDate);

        Optional<User> adminOpt =
                userRepository.findByRoleAndCityAndArea(Role.ADMIN, request.getCity(), request.getArea());

        if (adminOpt.isPresent()) {

            issue.setAssignedAdmin(adminOpt.get());

        } else {

            issue.setAssignedAdmin(null); // IMPORTANT

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
        }


        // ============================
        // 🧠 AI TRIAGE (REAL MAGIC)
        // ============================
        try {
            Map<String, String> aiResult =
                    aiTriageService.analyzeIssue(
                            request.getTitle(),
                            request.getDescription()
                    );

            issue.setAiSeverity(aiResult.get("severity"));
            issue.setAiDepartment(aiResult.get("department"));

        } catch (Exception e) {

        System.out.println("AI ERROR:");
        e.printStackTrace();

        issue.setAiSeverity("UNKNOWN");
        issue.setAiDepartment("OTHER");
    }

        Issue savedIssue = issueRepository.save(issue);

        return mapToResponse(savedIssue);
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

    private IssueResponse mapToResponse(Issue issue) {


        List<String> imageUrls =
                issue.getImages()
                        .stream()
                        .map(IssueImage::getImageUrl)
                        .toList();
        Integer verificationCount =
                (int) verificationRepository
                        .countByIssue(issue);

        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getLatitude(),
                issue.getLongitude(),
                issue.getCategory(),
                issue.getStatus(),
                issue.getCreatedAt(),
                issue.getReportedBy() != null
                        ? issue.getReportedBy().getEmail()
                        : null,
                imageUrls,
                issue.getAiSeverity(),
                issue.getAiDepartment(),
                issue.getIsDuplicate(),
                issue.getMasterIssueId(),
                verificationCount
        );
    }

    @Override
    public ImageUploadResponse uploadImage(
            Long issueId,
            MultipartFile file) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        try {

            Map<String, Object> uploadResult =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.emptyMap()
                    );

            String imageUrl =
                    uploadResult.get("secure_url").toString();

            IssueImage image = new IssueImage();

            image.setIssue(issue);
            image.setImageUrl(imageUrl);

            IssueImage saved =
                    issueImageRepository.save(image);

            return new ImageUploadResponse(
                    saved.getId(),
                    saved.getImageUrl()
            );

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed");
        }
    }

    @Override
    public IssueResponse updateIssueStatus(
            Long issueId,
            UpdateIssueStatusRequest request) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        issue.setStatus(request.getStatus());

        if (request.getStatus().name().equals("RESOLVED")) {
            issue.setResolvedAt(LocalDateTime.now());
        }

        Issue updatedIssue =
                issueRepository.save(issue);

        notificationService.createNotification(
                updatedIssue.getReportedBy(),
                "Your issue '" +
                        updatedIssue.getTitle() +
                        "' moved to " +
                        updatedIssue.getStatus()
        );

        messagingTemplate.convertAndSend(
                "/topic/issues",
                new IssueStatusUpdateMessage(
                        updatedIssue.getId(),
                        updatedIssue.getStatus().name()
                )
        );

        return mapToResponse(updatedIssue);
    }


    @Override
    public VerificationResponse verifyIssue(Long issueId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        if (verificationRepository
                .existsByIssueAndUser(issue, user)) {

            throw new RuntimeException(
                    "You already verified this issue");
        }

        Verification verification =
                new Verification();

        verification.setIssue(issue);
        verification.setUser(user);
        verification.setVerifiedAt(LocalDateTime.now());

        verificationRepository.save(verification);

        long count =
                verificationRepository.countByIssue(issue);

        return new VerificationResponse(
                issueId,
                (int) count
        );
    }


    @Override
    public CommentResponse addComment(
            Long issueId,
            CreateCommentRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        Comment comment = new Comment();

        comment.setCommentText(request.getCommentText());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setIssue(issue);
        comment.setUser(user);

        Comment saved =
                commentRepository.save(comment);

        return new CommentResponse(
                saved.getId(),
                saved.getCommentText(),
                saved.getUser().getEmail(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<CommentResponse> getCommentsByIssue(
            Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        return commentRepository.findByIssue(issue)
                .stream()
                .map(comment ->
                        new CommentResponse(
                                comment.getId(),
                                comment.getCommentText(),
                                comment.getUser().getEmail(),
                                comment.getCreatedAt()
                        ))
                .toList();
    }

}