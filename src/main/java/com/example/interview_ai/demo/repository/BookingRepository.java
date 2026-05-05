package com.example.interview_ai.demo.repository;

import com.example.interview_ai.demo.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔥 Check if slot already booked
    List<Booking> findByInterviewerEmail(String interviewerEmail);
    boolean existsByInterviewerIdAndDateAndSelectedSlot(
            Long interviewerId,
            String date,
            String selectedSlot
    );
}