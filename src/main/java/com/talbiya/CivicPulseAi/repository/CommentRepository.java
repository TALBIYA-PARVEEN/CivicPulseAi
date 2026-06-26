package com.talbiya.CivicPulseAi.repository;

import com.talbiya.CivicPulseAi.entity.Comment;
import com.talbiya.CivicPulseAi.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    List<Comment> findByIssue(Issue issue);
}
