package com.example.interview_ai.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIAnswerRequest {
    private String question;
    private String code;
    private String language;
    private String mode; // 👈 ADD THIS


}
