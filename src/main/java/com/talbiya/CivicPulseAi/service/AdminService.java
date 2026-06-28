package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.CreateAdminRequest;
import com.talbiya.CivicPulseAi.entity.User;

public interface AdminService {
    User createAdmin(CreateAdminRequest request);
}