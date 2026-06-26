package com.talbiya.CivicPulseAi.repository;

import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository
        extends JpaRepository<Verification, Long> {

    boolean existsByIssueAndUser(
            Issue issue,
            User user
    );

    long countByIssue(Issue issue);
}