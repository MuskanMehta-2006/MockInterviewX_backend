package com.example.interview_ai.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class AvailabilityDTO {

    private String date;
    private List<String> timeSlots;
}
