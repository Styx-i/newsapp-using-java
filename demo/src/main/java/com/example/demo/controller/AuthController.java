package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.service.EmailService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return "Email already exists";
        }
        user.setVerified(false);
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setPassword(user.getPassword());
        userService.save(user);
        // Send verification email
        String link = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), link);
        return "Account created. Please check your email to verify your account.";
    }


    @PostMapping("/login")
    public String login(@RequestBody java.util.Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        String password = payload.get("password");
        String captcha = payload.get("captcha");
        // Validate image-based captcha (answer stored as 'captcha' in session)
        String expected = (String) session.getAttribute("captcha");
        if (expected == null || captcha == null || !captcha.trim().equalsIgnoreCase(expected)) {
            return "Invalid captcha";
        }
        User dbUser = userService.findByEmail(email);
        if (dbUser != null && dbUser.getPassword().equals(password)) {
            if (!dbUser.isVerified()) {
                return "Please verify your email before logging in.";
            }
            session.setAttribute("userEmail", dbUser.getEmail());
            return "Login successful";
        }
        return "Invalid credentials";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token) {
        User user = userService.findByVerificationToken(token);
        if (user == null || user.isVerified() || user.getVerificationToken() == null) {
            return "This verification link is expired or already used.";
        }
        user.setVerified(true);
        user.setVerificationToken(null);
        userService.save(user);
        return "Email verified successfully. You can now log in.";
    }

}
