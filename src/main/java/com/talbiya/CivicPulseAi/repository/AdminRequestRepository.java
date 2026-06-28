package com.talbiya.CivicPulseAi.repository;

import com.talbiya.CivicPulseAi.entity.AdminRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {

    Optional<AdminRequest> findByCityAndArea(String city, String area);
}