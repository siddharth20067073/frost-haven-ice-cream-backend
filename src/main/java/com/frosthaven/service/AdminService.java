
package com.frosthaven.service;

import com.frosthaven.entity.Admin;
import com.frosthaven.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin login(String username, String password) {

        Admin admin = adminRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"));

        String storedPassword = admin.getPassword();

        // First, check a modern BCrypt password.
        if (passwordEncoder.matches(password, storedPassword)) {
            return admin;
        }

        // Temporarily support legacy 32-character MD5 hashes.
        // On success, immediately replace the old hash with BCrypt.
        if (storedPassword != null && storedPassword.matches("[a-fA-F0-9]{32}")) {
            String md5Password = createMD5(password);

            if (MessageDigest.isEqual(
                    md5Password.getBytes(StandardCharsets.UTF_8),
                    storedPassword.toLowerCase().getBytes(StandardCharsets.UTF_8))) {

                admin.setPassword(passwordEncoder.encode(password));
                return adminRepository.save(admin);
            }
        }

        throw new RuntimeException("Invalid username or password");
    }

    private String createMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to verify password", e);
        }
    }
}
