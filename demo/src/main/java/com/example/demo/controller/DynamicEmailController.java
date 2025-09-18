package com.example.demo.controller;

import com.example.demo.service.DynamicEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dynamic-email")
public class DynamicEmailController {
    @Autowired
    private DynamicEmailService emailService;

    @PostMapping
    public String sendDynamicEmail(@RequestBody Map<String, String> payload) {
        String fromEmail = payload.get("fromEmail");
        String appPassword = payload.get("appPassword");
        String toEmail = payload.get("toEmail");
        String subject = payload.get("subject");
        String text = payload.get("text");
        try {
            emailService.sendEmail(fromEmail, appPassword, toEmail, subject, text);
            return "Email sent successfully";
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}
