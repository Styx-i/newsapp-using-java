package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
public class PasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody User user) {
        User dbUser = userService.findByEmail(user.getEmail());
        if (dbUser == null) {
            return "Email not found";
        }
        String token = UUID.randomUUID().toString();
        dbUser.setResetToken(token);
        userService.save(dbUser);
        emailService.sendResetEmail(dbUser.getEmail(), token);
        return "Verification email sent";
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        User dbUser = userService.findByResetToken(token);
        if (dbUser == null) {
            return "Invalid token";
        }
        dbUser.setPassword(newPassword);
        dbUser.setResetToken(null);
        userService.save(dbUser);
        return "Password reset successful";
    }
}
