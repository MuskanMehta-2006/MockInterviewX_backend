package com.example.interview_ai.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TheoryFeedbackResponse {

    private int score;
    private String feedback;
    private List<Integer> scores; // 1 = correct, 0 = wrong

    // getters & setters
}
