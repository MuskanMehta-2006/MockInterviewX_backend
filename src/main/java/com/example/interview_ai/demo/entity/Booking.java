package com.example.interview_ai.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👇 Interviewee (jo book kar raha hai)
    private String intervieweeEmail;

    // 👇 Interviewer
    private String interviewerEmail;

    private Long interviewerId;

    private String date;

    // 👇 selected slot (clean naming)
    private String selectedSlot;

    // 👇 Razorpay
    private String paymentId;
    private String orderId;

    // 👇 booking status
    private String status; // PENDING / CONFIRMED / PAID

    // 👇 Google Meet link (IMPORTANT)
    private String meetLink;
}