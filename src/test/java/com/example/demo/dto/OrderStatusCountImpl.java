package com.example.demo.dto;

import java.util.Objects;

public class OrderStatusCountImpl implements OrderStatusCount {
    private String status;
    private Long count;

    public OrderStatusCountImpl() {}

    public OrderStatusCountImpl(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatusCountImpl that = (OrderStatusCountImpl) o;
        return Objects.equals(status, that.status) && Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, count);
    }
}
