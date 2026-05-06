package com.example.interview_ai.demo.service;

import com.example.interview_ai.demo.dto.LoginResponse;
import com.example.interview_ai.demo.entity.User;
import com.example.interview_ai.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ===============================
    // OTP STORAGE
    // ===============================
    private Map<String, String> otpStore = new HashMap<>();
    private Map<String, LocalDateTime> expiryStore = new HashMap<>();

    // ===============================
    // REGISTER
    // ===============================
    public User register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // ===============================
    // LOGIN
    // ===============================
    public LoginResponse login(String email, String password, String role) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 PASSWORD CHECK
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        // 🔥 ROLE CHECK (MAIN CHANGE)
        if (!user.getRole().equalsIgnoreCase(role)) {
            throw new RuntimeException(
                    "This email is already registered as " + user.getRole()
            );
        }

        // ✅ RESPONSE
        LoginResponse res = new LoginResponse();
        res.setMessage("Login Successful");
        res.setName(user.getName());
        res.setRole(user.getRole());
        res.setEmail(user.getEmail());

        return res;
    }

    // ===============================
    // GOOGLE LOGIN
    // ===============================
    public LoginResponse googleLogin(String email, String name, String role) {

        User user = userRepository.findByEmail(email).orElse(null);

        // 🔥 EXISTING USER
        if (user != null) {

            // ❌ ROLE CHANGE ALLOW MAT KARO
            if (!user.getRole().equalsIgnoreCase(role)) {
                throw new RuntimeException(
                        "This email is already registered as " + user.getRole()
                );
            }
        }
        else {
            // 🔥 NEW USER → create with GIVEN ROLE
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(""); // google login
            user.setRole(role);   // ✅ IMPORTANT FIX

            userRepository.save(user);
        }

        // ✅ RESPONSE
        LoginResponse res = new LoginResponse();
        res.setMessage("Google Login Success");
        res.setName(user.getName());
        res.setRole(user.getRole());
        res.setEmail(user.getEmail());

        return res;
    }
    public String sendOtp(String email, String role, String type) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        // ================= REGISTER =================
        if (type.equals("REGISTER")) {

            if (optionalUser.isPresent()) {

                User existingUser = optionalUser.get();

                if (existingUser.getRole().equals(role)) {
                    throw new RuntimeException("You are already registered");
                } else {
                    throw new RuntimeException("This email is registered with another role");
                }
            }
        }

        // ================= FORGOT =================
        if (type.equals("FORGOT")) {

            if (optionalUser.isEmpty()) {
                throw new RuntimeException("This email is not registered");
            }

            User existingUser = optionalUser.get();

            if (!existingUser.getRole().equals(role)) {
                throw new RuntimeException("This email is registered with another role");
            }
        }

        // ================= OTP GENERATION =================
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        otpStore.put(email, otp);
        expiryStore.put(email, LocalDateTime.now().plusMinutes(5));

        // ================= BREVO EMAIL =================
        try {

            String html =
                    "<div style='font-family:Arial;padding:20px'>" +
                            "<h2>MockInterviewX 🔐</h2>" +
                            "<p>Your OTP is:</p>" +
                            "<div style='font-size:22px;font-weight:bold;padding:10px;border:1px solid #ccc;display:inline-block;'>" +
                            otp +
                            "</div>" +
                            "<p>Valid for 5 minutes only</p>" +
                            "</div>";

            String safeHtml = html
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "");

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
          "subject": "MockInterviewX - OTP Verification",
          "htmlContent": "%s"
        }
        """.formatted(
                    System.getenv("BREVO_SENDER_EMAIL"),
                    email,
                    safeHtml
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

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }

        return "OTP sent successfully";
    }
    // ===============================
    // VERIFY OTP
    // ===============================
    public boolean verifyOtp(String email, String otp) {

        // ❌ no OTP found
        if (email == null || otp == null || !otpStore.containsKey(email)) {
            return false;
        }

        // ⏰ expiry check
        LocalDateTime expiryTime = expiryStore.get(email);

        if (expiryTime == null || expiryTime.isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            expiryStore.remove(email);
            return false;
        }

        // 🔐 OTP match check
        String storedOtp = otpStore.get(email);

        boolean isValid = storedOtp.equals(otp);

        // 🧹 cleanup (security best practice)
        if (isValid) {
            otpStore.remove(email);
            expiryStore.remove(email);
        }

        return isValid;
    }
    
    // ===============================
    // RESET PASSWORD
    // ===============================
    public String resetPassword(String email, String newPassword) {

        System.out.println("RESET API HIT");
        System.out.println("EMAIL: " + email);
        System.out.println("NEW PASSWORD: " + newPassword);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        return "Password reset successful";
    }
}