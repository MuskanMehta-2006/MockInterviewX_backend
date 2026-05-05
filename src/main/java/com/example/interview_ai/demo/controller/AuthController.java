package com.example.interview_ai.demo.controller;

import com.example.interview_ai.demo.dto.LoginResponse;
import com.example.interview_ai.demo.entity.User;
import com.example.interview_ai.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 🔥 REGISTER
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    // 🔥 LOGIN
    @PostMapping("/login")
    public LoginResponse login(@RequestBody User user) {
        return authService.login(user.getEmail(), user.getPassword(), user.getRole());
    }

    // 🔥 SEND OTP
    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        String role = req.get("role");   // ✅ ADD THIS
        String type = req.get("type");

        return authService.sendOtp(email, role, type);
    }

    // 🔥 VERIFY OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody Map<String, String> req) {
        boolean ok = authService.verifyOtp(req.get("email"), req.get("otp"));

        if (!ok) {
            throw new RuntimeException("Invalid or Expired OTP");
        }

        return "OTP Verified";
    }
    @PostMapping("/google-login")
    public LoginResponse googleLogin(@RequestBody Map<String, String> req) {
        return authService.googleLogin(
                req.get("email"),
                req.get("name"),
                req.get("role") // 🔥 add this
        );
    }
    // 🔥 RESET PASSWORD
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> req) {

        System.out.println("RESET API HIT"); // 👈 MUST PRINT NOW

        String email = req.get("email");
        String newPassword = req.get("newPassword");

        System.out.println("EMAIL: " + email);
        System.out.println("NEW PASSWORD: " + newPassword);

        return authService.resetPassword(email, newPassword);
    }
}