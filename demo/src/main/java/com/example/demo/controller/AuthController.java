package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return "Email already exists";
        }
        userService.save(user);
        return "Account created";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user, HttpSession session) {
        User dbUser = userService.findByEmail(user.getEmail());
        if (dbUser != null && dbUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("userEmail", dbUser.getEmail());
            return "Login successful";
        }
        return "Invalid credentials";
    }

}
