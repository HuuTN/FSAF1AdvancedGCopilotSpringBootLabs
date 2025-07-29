package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration for the ecommerce platform.
 * Configures authentication, authorization, and security policies.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                UserDetails admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder().encode("admin123"))
                                .roles("ADMIN")
                                .build();

                UserDetails manager = User.builder()
                                .username("manager")
                                .password(passwordEncoder().encode("manager123"))
                                .roles("MANAGER")
                                .build();

                UserDetails user = User.builder()
                                .username("user")
                                .password(passwordEncoder().encode("user123"))
                                .roles("USER")
                                .build();

                return new InMemoryUserDetailsManager(admin, manager, user);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", configuration);
                return source;
        }

        @SuppressWarnings("removal")
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable()) // For API endpoints
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS
                                                                                                   // configuration
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                .requestMatchers("/api/v1/products/**").permitAll()
                                                .requestMatchers("/api/v1/categories/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/customers").permitAll() // Only
                                                                                                                   // customer
                                                                                                                   // registration
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll() // User
                                                                                                               // registration
                                                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll() // Public
                                                                                                                   // read
                                                                                                                   // for
                                                                                                                   // reviews
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/actuator/health", "/actuator/info").permitAll() // Actuator
                                                                                                                   // endpoints

                                                // Admin-only endpoints
                                                .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ADMIN", "MANAGER")
                                                .requestMatchers("/api/v1/customers/**").hasRole("ADMIN") // Restrict
                                                                                                          // customer
                                                                                                          // management
                                                                                                          // to admins

                                                // Authenticated endpoints
                                                .requestMatchers("/api/v1/reviews/**").authenticated() // Write
                                                                                                       // operations
                                                                                                       // require auth
                                                .requestMatchers("/api/v1/orders/**").authenticated() // Orders require
                                                                                                      // authentication

                                                // All other endpoints require authentication
                                                .anyRequest().authenticated())
                                .httpBasic(httpBasic -> {
                                }) // Use HTTP Basic Authentication for simplicity
                                .headers(headers -> headers.frameOptions().disable()); // For H2 console

                return http.build();
        }
}
