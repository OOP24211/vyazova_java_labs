package com.example.watchtogether;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует!");
        }

        userRepository.save(user);
        return ResponseEntity.ok("Успешная регистрация!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> dbUser = userRepository.findByUsername(user.getUsername());

        if (dbUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Пользователь не найден!");
        }

        if (!dbUser.get().getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().body("Неверный пароль!");
        }

        return ResponseEntity.ok("Успешный вход!");
    }
}
