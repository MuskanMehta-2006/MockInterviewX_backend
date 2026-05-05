package com.example.interview_ai.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;

    @ElementCollection
    @CollectionTable(
            name = "interviewer_time_slots",
            joinColumns = @JoinColumn(name = "availability_id")
    )
    @Column(name = "time_slot")
    private List<String> timeSlots;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;
}