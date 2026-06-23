package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.LoginRequest;
import com.talbiya.CivicPulseAi.dto.LoginResponse;
import com.talbiya.CivicPulseAi.dto.RegisterRequest;
import com.talbiya.CivicPulseAi.dto.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;



public interface UserService {
    RegisterResponse registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
}