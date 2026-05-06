package com.example.interview_ai.demo.service;

import com.example.interview_ai.demo.entity.Booking;
import com.example.interview_ai.demo.repository.BookingRepository;
import com.razorpay.*;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookingService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Autowired
    private BookingRepository bookingRepository;

    // =========================================
    // 🔥 CREATE ORDER
    // =========================================
    public Map<String, Object> createOrder(Map<String, Object> data) {
        try {
            int amount = Integer.parseInt(data.get("amount").toString());

            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject options = new JSONObject();
            options.put("amount", amount * 100);
            options.put("currency", "INR");
            options.put("receipt", "order_" + System.currentTimeMillis());

            Order order = client.orders.create(options);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", amount);
            response.put("key", keyId);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Order creation failed");
        }
    }

    // =========================================
    // 🔥 CONFIRM BOOKING (FULL FLOW)
    // =========================================
    public Booking confirmBooking(Booking booking) {

        System.out.println("BOOKING DATA: " + booking.getIntervieweeEmail());
        System.out.println("DATE: " + booking.getDate());
        System.out.println("SLOT: " + booking.getSelectedSlot());

        // ❌ VALIDATION
        if (booking.getIntervieweeEmail() == null || booking.getIntervieweeEmail().isEmpty()) {
            throw new RuntimeException("Interviewee email missing");
        }

        if (booking.getInterviewerEmail() == null || booking.getInterviewerEmail().isEmpty()) {
            throw new RuntimeException("Interviewer email missing");
        }

        if (booking.getDate() == null || booking.getSelectedSlot() == null) {
            throw new RuntimeException("Date or Slot missing");
        }

        // 1. SLOT CHECK
        boolean exists = bookingRepository.existsByInterviewerIdAndDateAndSelectedSlot(
                booking.getInterviewerId(),
                booking.getDate(),
                booking.getSelectedSlot()
        );

        if (exists) {
            throw new RuntimeException("Slot already booked");
        }

        // 2. MEET LINK GENERATE
        String meetLink = generateMeetLink();
        booking.setMeetLink(meetLink);

        // 3. STATUS SET
        booking.setStatus("CONFIRMED");
        System.out.println("Status confirmed");

        // 4. SAVE BOOKING
        Booking saved = bookingRepository.save(booking);

        // 5. EMAIL TO INTERVIEWEE
        sendEmail(
                booking.getIntervieweeEmail(),
                booking,
                "Interview Confirmed 🎉"
        );

        // 6. EMAIL TO INTERVIEWER
        sendEmail(
                booking.getInterviewerEmail(),
                booking,
                "Interview Scheduled 📅"
        );

        return saved;
    }
    // =========================================
    // 🔥 EMAIL SENDER (LIKE YOUR AUTH SERVICE)
    // =========================================
    private void sendEmail(String to, Booking booking, String subject) {

        try {

            System.out.println("📧 BREVO EMAIL STARTED");
            System.out.println("➡️ To: " + to);

            String html =
                    "<div style='font-family:Arial;padding:20px'>" +
                            "<h2>Interview Booking Confirmed 🚀</h2>" +
                            "<p><b>Date:</b> " + booking.getDate() + "</p>" +
                            "<p><b>Time Slot:</b> " + booking.getSelectedSlot() + "</p>" +
                            "<p><b>Meet Link:</b> <a href='" + booking.getMeetLink() + "'>Join Meeting</a></p>" +
                            "<br><p>All the best 💪</p>" +
                            "</div>";

            String requestBody = """
        {
          "sender": {
            "name": "MockInterviewX",
            "email": "%s"
          },
          "to": [
            {
              "email": "%s"
            }
          ],
          "subject": "%s",
          "htmlContent": "%s"
        }
        """.formatted(
                    System.getenv("BREVO_SENDER_EMAIL"),
                    to,
                    subject,
                    html.replace("\"", "\\\"").replace("\n", "")
            );

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("api-key", System.getenv("BREVO_API_KEY"));

            org.springframework.http.HttpEntity<String> request =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            org.springframework.web.client.RestTemplate restTemplate =
                    new org.springframework.web.client.RestTemplate();

            restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

            System.out.println("✅ EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email failed: " + e.getMessage());
        }
    }
    // =========================================
    // 🔥 MEET LINK GENERATOR
    // =========================================
    private String generateMeetLink() {
        return "https://meet.google.com/" +
                UUID.randomUUID().toString().substring(0, 3) + "-" +
                UUID.randomUUID().toString().substring(0, 3) + "-" +
                UUID.randomUUID().toString().substring(0, 3);
    }
    public List<Booking> getBookingsByInterviewerEmail(String email) {
        return bookingRepository.findByInterviewerEmail(email);
    }
}