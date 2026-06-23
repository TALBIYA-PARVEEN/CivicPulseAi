package com.talbiya.CivicPulseAi.service;

import com.talbiya.CivicPulseAi.dto.LoginRequest;
import com.talbiya.CivicPulseAi.dto.LoginResponse;
import com.talbiya.CivicPulseAi.dto.RegisterRequest;
import com.talbiya.CivicPulseAi.dto.RegisterResponse;
import com.talbiya.CivicPulseAi.entity.User;
import com.talbiya.CivicPulseAi.enums.Role;
import com.talbiya.CivicPulseAi.repository.UserRepository;
import com.talbiya.CivicPulseAi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {

        // check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // create user entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.CITIZEN);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(token);
    }
}