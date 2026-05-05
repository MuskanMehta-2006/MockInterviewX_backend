package com.example.interview_ai.demo.controller;

import com.example.interview_ai.demo.dto.InterviewerUpdateRequest;
import com.example.interview_ai.demo.dto.AvailabilityDTO;
import com.example.interview_ai.demo.entity.Interviewer;
import com.example.interview_ai.demo.service.InterviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviewer")
public class InterviewerController {

    @Autowired
    private InterviewerService service;

    // 🔥 GET PROFILE BY EMAIL
    @GetMapping("/by-email")
    public Interviewer getInterviewer(@RequestParam String email) {
        return service.getByEmail(email);
    }

    // 🔥 CREATE OR UPDATE PROFILE (EMAIL BASED)
    @PutMapping
    public Interviewer updateInterviewer(
            @RequestBody InterviewerUpdateRequest request
    ) {
        return service.updateInterviewer(request);
    }
    @GetMapping("/all")
    public List<Interviewer> getAllInterviewers() {
        return service.getAllInterviewers();
    }


    // 🔥 UPDATE ONLY AVAILABILITY (EMAIL BASED)
    @PatchMapping("/availability")
    public Interviewer updateAvailability(
            @RequestParam String email,
            @RequestBody List<AvailabilityDTO> availability
    ) {
        return service.updateAvailabilityByEmail(email, availability);
    }
}