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

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

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
    // SEND OTP (EMAIL HTML FORMAT)
    // ===============================
    public String sendOtp(String email, String role, String type) {

        // 🔍 Find user
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // ================= REGISTER =================
        if (type.equals("REGISTER")) {

            if (optionalUser.isPresent()) {

                User existingUser = optionalUser.get();

                // ❌ Same role
                if (existingUser.getRole().equals(role)) {
                    throw new RuntimeException("You are already registered");
                }

                // ❌ Different role
                else {
                    throw new RuntimeException("This email is registered with another role");
                }
            }
        }

        // ================= FORGOT =================
        if (type.equals("FORGOT")) {

            // ❌ Not registered
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("This email is not registered");
            }

            User existingUser = optionalUser.get();

            // ❌ Role mismatch
            if (!existingUser.getRole().equals(role)) {
                throw new RuntimeException("This email is registered with another role");
            }
        }

        // ================= OTP GENERATION =================
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        otpStore.put(email, otp);
        expiryStore.put(email, LocalDateTime.now().plusMinutes(5));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("MockInterviewX - OTP Verification");

            String htmlBody =
                    "<div style='font-family:Arial;padding:20px'>"
                            + "<h2>MockInterviewX</h2>"
                            + "<p>Your OTP is:</p>"
                            + "<div style='font-size:22px;font-weight:bold;padding:10px;border:1px solid #ccc;display:inline-block;'>"
                            + otp +
                            "</div>"
                            + "<p>Valid for 5 minutes only</p>"
                            + "</div>";

            helper.setText(htmlBody, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed: " + e.getMessage());
        }

        return "OTP sent successfully";
    }

    // ===============================
    // VERIFY OTP
    // ===============================
    public boolean verifyOtp(String email, String otp) {

        if (!otpStore.containsKey(email)) return false;

        if (expiryStore.get(email).isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            expiryStore.remove(email);
            return false;
        }

        return otpStore.get(email).equals(otp);
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