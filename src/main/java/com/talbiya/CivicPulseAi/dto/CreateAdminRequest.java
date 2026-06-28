package com.talbiya.CivicPulseAi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdminRequest {

    private String name;
    private String email;
    private String password;

    private String city;
    private String area;
}