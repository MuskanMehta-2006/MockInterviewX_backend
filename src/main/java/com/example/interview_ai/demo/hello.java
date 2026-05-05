package com.example.interview_ai.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {

    @GetMapping("/")
    public String home() {
        return "AI Interview Backend Running 🚀";
    }
}