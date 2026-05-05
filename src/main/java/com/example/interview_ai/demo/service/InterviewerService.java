package com.example.interview_ai.demo.service;

import com.example.interview_ai.demo.dto.InterviewerUpdateRequest;
import com.example.interview_ai.demo.dto.AvailabilityDTO;
import com.example.interview_ai.demo.entity.Interviewer;
import com.example.interview_ai.demo.entity.InterviewerAvailability;
import com.example.interview_ai.demo.repository.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewerService {

    @Autowired
    private InterviewerRepository repository;

    // 🔥 GET BY EMAIL
    public Interviewer getByEmail(String email) {
        return repository.findByEmail(email);
    }

    // 🔥 CREATE OR UPDATE PROFILE (EMAIL BASED)
    public Interviewer updateInterviewer(InterviewerUpdateRequest req) {

        Interviewer interviewer = repository.findByEmail(req.getEmail());

        // 🆕 FIRST TIME → CREATE
        if (interviewer == null) {
            interviewer = new Interviewer();
            interviewer.setEmail(req.getEmail());
        }

        // 🧑 PROFILE FIELDS
        interviewer.setName(req.getName());
        interviewer.setPassword(req.getPassword());
        interviewer.setIntro(req.getIntro());
        interviewer.setSpeciality(req.getSpeciality());

        interviewer.setExperience(req.getExperience());
        interviewer.setPrice(req.getPrice());

        interviewer.setLinkedin(req.getLinkedin());
        interviewer.setGithub(req.getGithub());

        interviewer.setBio(req.getBio());
        interviewer.setLanguages(req.getLanguages());
        interviewer.setRating(req.getRating());

        // 🔥 AVAILABILITY
        if (interviewer.getAvailability() != null) {
            interviewer.getAvailability().clear();
        } else {
            interviewer.setAvailability(new ArrayList<>());
        }

        if (req.getAvailability() != null) {
            for (AvailabilityDTO dto : req.getAvailability()) {

                InterviewerAvailability a = new InterviewerAvailability();
                a.setDate(dto.getDate());
                a.setTimeSlots(dto.getTimeSlots());
                a.setInterviewer(interviewer);

                interviewer.getAvailability().add(a);
            }
        }

        return repository.save(interviewer);
    }

    // 🔥 UPDATE ONLY AVAILABILITY (EMAIL BASED)
    public Interviewer updateAvailabilityByEmail(String email, List<AvailabilityDTO> availabilityList) {

        Interviewer interviewer = repository.findByEmail(email);

        if (interviewer == null) {
            throw new RuntimeException("Interviewer not found with email: " + email);
        }

        // clear old availability
        if (interviewer.getAvailability() != null) {
            interviewer.getAvailability().clear();
        } else {
            interviewer.setAvailability(new ArrayList<>());
        }

        // add new availability
        if (availabilityList != null) {
            for (AvailabilityDTO dto : availabilityList) {

                InterviewerAvailability a = new InterviewerAvailability();
                a.setDate(dto.getDate());
                a.setTimeSlots(dto.getTimeSlots());
                a.setInterviewer(interviewer);

                interviewer.getAvailability().add(a);
            }
        }

        return repository.save(interviewer);
    }

    public List<Interviewer> getAllInterviewers() {
        return repository.findAll();
    }
}