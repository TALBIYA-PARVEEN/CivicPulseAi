package com.talbiya.CivicPulseAi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "issue_images")
public class IssueImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;
}