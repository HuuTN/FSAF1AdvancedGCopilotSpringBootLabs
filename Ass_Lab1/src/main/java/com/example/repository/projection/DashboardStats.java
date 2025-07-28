package com.example.repository.projection;

import java.math.BigDecimal;

public interface DashboardStats {
    BigDecimal getTotalRevenue();
    Long getTotalOrders();
    Long getNewCustomersThisMonth();
}
