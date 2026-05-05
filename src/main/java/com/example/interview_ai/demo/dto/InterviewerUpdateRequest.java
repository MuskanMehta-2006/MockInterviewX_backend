package com.example.interview_ai.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class InterviewerUpdateRequest {

    private String name;
    private String email;
    private String password;

    private String intro;
    private String speciality;

    private int experience;
    private int price;

    private String linkedin;
    private String github;

    private String bio;
    private String languages;

    private String rating;

    private List<AvailabilityDTO> availability;
}
