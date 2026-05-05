package com.example.interview_ai.demo.dto;
public class AIQuestionRequest {
    private String type;   // dsa, fundamentals, system-design
    private String level;  // easy, medium, hard

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
