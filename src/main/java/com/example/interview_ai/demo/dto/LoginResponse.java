package com.example.interview_ai.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String message;
    private String role;
    private String name;
    private String email;

    // getters & setters
}