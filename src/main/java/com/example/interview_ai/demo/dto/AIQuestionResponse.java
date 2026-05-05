package com.example.interview_ai.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AIQuestionResponse {
    public String question;
    public String statement;
    public Example example1;
    public Example example2;
    public List<String> constraints;
}

class Example {
    public String input;
    public String output;
}