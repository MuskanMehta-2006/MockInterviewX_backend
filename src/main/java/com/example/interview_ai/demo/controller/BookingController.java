package com.example.interview_ai.demo.controller;

import com.example.interview_ai.demo.entity.Booking;
import com.example.interview_ai.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")// frontend connect ke liye
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // 🔥 CREATE ORDER
    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) {
        return bookingService.createOrder(data);
    }

    // 🔥 CONFIRM BOOKING AFTER PAYMENT
    @PostMapping("/confirm")
    public Booking confirmBooking(@RequestBody Booking booking) {
        return bookingService.confirmBooking(booking);
    }
    @GetMapping("/interviewer")
    public List<Booking> getInterviewerBookings(@RequestParam String email) {
        return bookingService.getBookingsByInterviewerEmail(email);
    }
}