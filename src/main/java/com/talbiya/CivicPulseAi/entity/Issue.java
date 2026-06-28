package com.talbiya.CivicPulseAi.entity;

import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.enums.IssueCategory;
import com.talbiya.CivicPulseAi.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "issues")
@Data
public class Issue {

    @ManyToOne
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    @Column(name = "is_duplicate")
    private Boolean isDuplicate = false;

    @Column(name = "master_issue_id")
    private Long masterIssueId;

    @Column(name = "ai_severity")
    private String aiSeverity;

    @Column(name = "ai_department")
    private String aiDepartment;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private IssueCategory category;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL
    )
    private List<IssueImage> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL
    )
    private List<Verification> verifications;

    @OneToMany(
            mappedBy = "issue",
            cascade = CascadeType.ALL
    )
    private List<Comment> comments;

    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    private String city;
    private String area;


}