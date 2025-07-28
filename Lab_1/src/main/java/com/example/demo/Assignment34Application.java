package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Assignment34Application {
    public static void main(String[] args) {
        SpringApplication.run(Assignment34Application.class, args);
    }
}
