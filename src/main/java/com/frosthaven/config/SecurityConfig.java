
package com.frosthaven.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Customer pages
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/style.css",
                    "/script.js",
                    "/images/**",
                    "/pages/**"
                ).permitAll()

                // Admin pages
                .requestMatchers(
                    "/admin/**"
                ).permitAll()

                // Public product and category APIs
                .requestMatchers(
                    "/api/products",
                    "/api/products/**",
                    "/api/categories",
                    "/api/categories/**"
                ).permitAll()

                // Customer authentication APIs
                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()

                // Admin login API
                .requestMatchers(
                    "/api/admin/login"
                ).permitAll()

                // Admin users API
                .requestMatchers(
                    "/api/admin/users",
                    "/api/admin/users/**"
                ).permitAll()

                // Cart and order APIs
                .requestMatchers(
                    "/api/cart/**",
                    "/api/orders/**"
                ).permitAll()

                // Other requests require authentication
                .anyRequest().authenticated()
            )

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
