package com.talbiya.CivicPulseAi.controller;

import com.talbiya.CivicPulseAi.dto.LoginRequest;
import com.talbiya.CivicPulseAi.dto.LoginResponse;
import com.talbiya.CivicPulseAi.dto.RegisterRequest;
import com.talbiya.CivicPulseAi.dto.RegisterResponse;
import com.talbiya.CivicPulseAi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }
}