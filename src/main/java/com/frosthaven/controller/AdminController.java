package com.frosthaven.controller;

import com.frosthaven.entity.Admin;
import com.frosthaven.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AdminLoginRequest request) {

        try {

            Admin admin = adminService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            // Never send the password back to the browser.
            admin.setPassword(null);

            return ResponseEntity.ok(admin);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    public static class AdminLoginRequest {

        private String username;
        private String password;

        public AdminLoginRequest() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}