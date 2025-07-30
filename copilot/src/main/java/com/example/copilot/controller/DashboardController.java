package com.example.copilot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.copilot.dto.DashboardStatsDTO;
import com.example.copilot.repository.OrderRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsDTO getDashboardStats() {
        return orderRepository.getDashboardStats();
    }
}

