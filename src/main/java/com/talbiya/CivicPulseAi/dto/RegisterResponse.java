package com.talbiya.CivicPulseAi.dto;

import com.talbiya.CivicPulseAi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}