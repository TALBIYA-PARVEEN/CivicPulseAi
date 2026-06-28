package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.CreateAdminRequest;
import com.talbiya.CivicPulseAi.entity.AdminRequest;
import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.enums.RequestStatus;
import com.talbiya.CivicPulseAi.enums.Role;
import com.talbiya.CivicPulseAi.repository.AdminRequestRepository;
import com.talbiya.CivicPulseAi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRequestRepository adminRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminAssignmentService adminAssignmentService;

    @Override
    public User createAdmin(CreateAdminRequest request) {

        User admin = new User();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);
        admin.setCity(request.getCity());
        admin.setArea(request.getArea());

        User savedAdmin = userRepository.save(admin);

// assign issues
        adminAssignmentService.assignPendingIssues(
                savedAdmin.getCity(),
                savedAdmin.getArea(),
                savedAdmin
        );

// ⭐ UPDATE ADMIN REQUEST STATUS
        AdminRequest req = adminRequestRepository
                .findByCityAndArea(savedAdmin.getCity(), savedAdmin.getArea())
                .orElse(null);

        if (req != null) {
            req.setStatus(RequestStatus.ASSIGNED);
            req.setAssignedAdmin(savedAdmin);
            req.setUpdatedAt(LocalDateTime.now());

            adminRequestRepository.save(req);
        }

        return savedAdmin;
    }
}
