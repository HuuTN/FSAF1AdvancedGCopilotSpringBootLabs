package com.example.copilot.service;

import com.example.copilot.core.dto.DashboardStatsDTO;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Dashboard Service Unit Tests - TDD Approach
 * Testing optimized single-query dashboard statistics
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private List<Object[]> mockDashboardData;

    @BeforeEach
    void setUp() {
        // Mock data: [totalRevenue, totalOrders, newCustomersThisMonth]
        Object[] row = new Object[]{
            new BigDecimal("15000.50"),  // totalRevenue
            25L,                         // totalOrders
            8L                           // newCustomersThisMonth
        };
        mockDashboardData = new ArrayList<>();
        mockDashboardData.add(row);
    }

    @Test
    void getDashboardStats_ShouldReturnCorrectStats_WhenDataExists() {
        // Arrange
        when(orderRepository.getDashboardStats()).thenReturn(mockDashboardData);

        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("15000.50"), result.getTotalRevenue());
        assertEquals(25L, result.getTotalOrders());
        assertEquals(8L, result.getNewCustomersThisMonth());
        
        // Verify single database call (Optimized Prompt)
        verify(orderRepository, times(1)).getDashboardStats();
    }

    @Test
    void getDashboardStats_ShouldHandleZeroValues_WhenNoDataExists() {
        // Arrange - Mock zero values
        Object[] zeroRow = {BigDecimal.ZERO, 0L, 0L};
        List<Object[]> zeroData = new ArrayList<>();
        zeroData.add(zeroRow);
        when(orderRepository.getDashboardStats()).thenReturn(zeroData);

        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(0L, result.getNewCustomersThisMonth());
        
        verify(orderRepository, times(1)).getDashboardStats();
    }

    @Test
    void getDashboardStats_ShouldHandleIntegerToLongConversion_WhenDatabaseReturnsIntegers() {
        // Arrange - Mock Integer values (common database return type)
        Object[] integerRow = {
            new BigDecimal("5000.75"),
            Integer.valueOf(10),     // totalOrders as Integer
            Integer.valueOf(3)       // newCustomersThisMonth as Integer
        };
        List<Object[]> integerData = new ArrayList<>();
        integerData.add(integerRow);
        when(orderRepository.getDashboardStats()).thenReturn(integerData);

        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.75"), result.getTotalRevenue());
        assertEquals(10L, result.getTotalOrders());
        assertEquals(3L, result.getNewCustomersThisMonth());
        
        verify(orderRepository, times(1)).getDashboardStats();
    }

    @Test
    void getDashboardStats_ShouldVerifyOptimizedApproach_SingleDatabaseCall() {
        // Arrange
        when(orderRepository.getDashboardStats()).thenReturn(mockDashboardData);

        // Act
        dashboardService.getDashboardStats();

        // Assert - Verify that only ONE database call is made (Optimized Prompt)
        verify(orderRepository, times(1)).getDashboardStats();
        verifyNoMoreInteractions(orderRepository);
    }
}
