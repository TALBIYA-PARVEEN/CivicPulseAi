package com.talbiya.CivicPulseAi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.talbiya.CivicPulseAi.dto.*;
import com.talbiya.CivicPulseAi.entity.*;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import com.talbiya.CivicPulseAi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class IssueServiceImpl implements IssueService {

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
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setCategory(request.getCategory());

        issue.setStatus(IssueStatus.REPORTED);
        issue.setCreatedAt(LocalDateTime.now());
        issue.setReportedBy(user);

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
                imageUrls
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

        Issue updatedIssue =
                issueRepository.save(issue);

        notificationService.createNotification(
                updatedIssue.getReportedBy(),
                "Your issue '" +
                        updatedIssue.getTitle() +
                        "' moved to " +
                        updatedIssue.getStatus()
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