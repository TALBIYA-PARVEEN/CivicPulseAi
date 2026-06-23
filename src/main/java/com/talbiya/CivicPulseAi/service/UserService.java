package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.RegisterRequest;
import com.talbiya.CivicPulseAi.dto.RegisterResponse;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest request);
}