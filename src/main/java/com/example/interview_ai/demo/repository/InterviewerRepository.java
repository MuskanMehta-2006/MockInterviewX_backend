package com.example.interview_ai.demo.repository;

import com.example.interview_ai.demo.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {

    Interviewer findByEmailAndPassword(String email, String password);

    Interviewer findByEmail(String email);

    Interviewer findByUserId(Long userId);
}