package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable for API testing, enable in production
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/v1/products/**").permitAll()
                .requestMatchers("/api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
                
                // Dashboard - Admin/Manager only
                .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ADMIN", "MANAGER")
                
                // Review creation - Authenticated users only
                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").hasRole("USER")
                
                // Swagger/OpenAPI docs
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // H2 Console for development
                .requestMatchers("/h2-console/**").permitAll()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {})
            .headers(headers -> headers.frameOptions().disable()); // For H2 console

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN", "USER")
            .build();

        UserDetails manager = User.builder()
            .username("manager")
            .password(passwordEncoder().encode("manager123"))
            .roles("MANAGER", "USER")
            .build();

        UserDetails user1 = User.builder()
            .username("user1")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        UserDetails user2 = User.builder()
            .username("user2")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, manager, user1, user2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
