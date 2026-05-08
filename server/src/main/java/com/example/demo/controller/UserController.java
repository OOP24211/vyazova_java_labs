package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            System.out.println("User already exists: " + user.getUsername());
            return existingUser.get();
        }
        System.out.println("Creating new user: " + user.getUsername());
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Optional<User> foundOptional = userRepository.findByUsername(user.getUsername());
        if (foundOptional.isPresent() && foundOptional.get().getPassword().equals(user.getPassword())) {
            return "Login successful";
        }
        return "login failed";
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}