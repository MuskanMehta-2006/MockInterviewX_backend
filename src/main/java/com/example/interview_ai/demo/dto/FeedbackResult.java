package com.example.interview_ai.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackResult {
    private String timeComplexity;
    private String spaceComplexity;
    private String codeQuality;
    private int score;
    private String feedback;
}
