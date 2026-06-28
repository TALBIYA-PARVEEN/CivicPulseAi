package com.talbiya.CivicPulseAi.repository;

import com.talbiya.CivicPulseAi.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findTop20ByOrderByCreatedAtDesc();

    List<Issue> findByIsDuplicateFalseOrderByCreatedAtDesc();

    List<Issue> findByCityAndAreaAndAssignedAdminIsNull(String city, String area);



}
