package com.example.interview_ai.demo.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TheoryFeedbackRequest {

    private String type;
    private String level;
    private List<String> questions;
    private List<String> answers;

    // getters & setters
}