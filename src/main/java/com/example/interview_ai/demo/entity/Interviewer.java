package com.example.interview_ai.demo.entity;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;
    private String email;
    private String password;

    private String intro;
    private String speciality;

    private int experience;
    private int price;

    private String linkedin;
    private String github;

    private String bio;
    private String languages;

    private String rating;

    // 🔥 NEW RELATION
    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewerAvailability> availability;
}