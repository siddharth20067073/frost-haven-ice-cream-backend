package com.frosthaven.controller;

import com.frosthaven.entity.User;
import com.frosthaven.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthLoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthLoginController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            User user = userRepository
                    .findByEmail(request.getEmail())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Invalid email or password"));

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) {

                throw new RuntimeException(
                        "Invalid email or password");
            }

            user.setPassword(null);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    public static class LoginRequest {

        private String email;
        private String password;

        public LoginRequest() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}