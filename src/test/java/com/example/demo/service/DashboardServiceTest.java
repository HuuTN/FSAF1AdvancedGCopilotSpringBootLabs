package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DashboardServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProductRepository productRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dashboardService = new DashboardServiceImpl(
            orderRepository,
            userRepository,
            productRepository
        );
    }

    @Test
    void getDetailedDashboardData_ShouldReturnCompleteData() {
        // Prepare mock data
        DashboardStatsDTO mockStats = new DashboardStatsDTO(5000.0, 100L, 50L);
        
        List<TopProductDTO> mockTopProducts = Arrays.asList(
            new TopProductDTO(1L, "Product 1", 10L, 1000.0),
            new TopProductDTO(2L, "Product 2", 8L, 800.0)
        );
        
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<RecentOrderDTO> mockRecentOrders = Arrays.asList(
            new RecentOrderDTO(1L, now, "DELIVERED", "User1", 2L, 100.0),
            new RecentOrderDTO(2L, now, "PENDING", "User2", 3L, 200.0)
        );
        
        // Set up mock repository responses
        when(orderRepository.getDashboardStats()).thenReturn(mockStats);
        when(orderRepository.getTopSellingProducts(any(Pageable.class))).thenReturn(mockTopProducts);
        when(orderRepository.getRecentOrders(any(Pageable.class))).thenReturn(mockRecentOrders);
        List<OrderStatusCount> mockStatusCounts = mockRecentOrders.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                RecentOrderDTO::getStatus,
                java.util.stream.Collectors.counting()
            ))
            .entrySet()
            .stream()
            .map(entry -> {
                OrderStatusCountImpl count = new OrderStatusCountImpl();
                count.setStatus(entry.getKey());
                count.setCount(entry.getValue());
                return count;
            })
            .collect(java.util.stream.Collectors.toList());
            
        when(orderRepository.getOrderCountByStatus()).thenReturn(mockStatusCounts);

        // Execute service method
        DashboardDataDTO result = dashboardService.getDetailedDashboardData(5, 10);

        // Verify results
        assertNotNull(result);
        assertEquals(mockStats, result.getStats());
        assertEquals(mockTopProducts, result.getTopProducts());
        assertEquals(mockRecentOrders, result.getRecentOrders());
        
        // Verify status counts
        Map<String, Long> expectedStatusCounts = mockRecentOrders.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                RecentOrderDTO::getStatus,
                java.util.stream.Collectors.counting()
            ));
        assertEquals(expectedStatusCounts.size(), result.getOrdersByStatus().size());
        
        // Today's revenue is calculated from recent orders
        double expectedTodayRevenue = mockRecentOrders.stream()
            .filter(order -> order.getOrderDate().toLocalDate().isEqual(now.toLocalDate()))
            .mapToDouble(RecentOrderDTO::getTotalAmount)
            .sum();
        assertEquals(expectedTodayRevenue, result.getTodayRevenue());

        // Verify repository method calls
        verify(orderRepository).getDashboardStats();
        verify(orderRepository).getTopSellingProducts(any(Pageable.class));
        verify(orderRepository, times(2)).getRecentOrders(any(Pageable.class)); // Called twice due to today's revenue calculation
        verify(orderRepository).getOrderCountByStatus();
    }

    @Test
    void getDetailedDashboardData_WhenNoData_ShouldReturnEmptyLists() {
        // Set up mock repository to return empty or null values
        when(orderRepository.getDashboardStats())
            .thenReturn(new DashboardStatsDTO(0.0, 0L, 0L));
        when(orderRepository.getTopSellingProducts(any(Pageable.class)))
            .thenReturn(List.of());
        when(orderRepository.getRecentOrders(any(Pageable.class)))
            .thenReturn(List.of());
        when(orderRepository.getOrderCountByStatus())
            .thenReturn(List.of());

        // Execute service method
        DashboardDataDTO result = dashboardService.getDetailedDashboardData(5, 10);

        // Verify results
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertTrue(result.getTopProducts().isEmpty());
        assertTrue(result.getRecentOrders().isEmpty());
        assertTrue(result.getOrdersByStatus().isEmpty());
        assertEquals(0.0, result.getTodayRevenue());
    }
}
