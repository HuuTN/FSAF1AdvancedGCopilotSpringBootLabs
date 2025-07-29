package com.example.health;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxMemoryHealthIndicatorTest {

    @Test
    void healthIndicatorShouldBeInstantiable() {
        // Given & When
        MaxMemoryHealthIndicator indicator = new MaxMemoryHealthIndicator();

        // Then
        assertThat(indicator).isNotNull();
    }

    @Test
    void healthMethodShouldReturnHealthObject() {
        // Given
        MaxMemoryHealthIndicator indicator = new MaxMemoryHealthIndicator();

        // When
        var health = indicator.health();

        // Then
        assertThat(health).isNotNull();
        assertThat(health.getDetails()).isNotEmpty();

        // Verify expected details are present
        assertThat(health.getDetails()).containsKeys(
                "maxMemory", "totalMemory", "usedMemory", "freeMemory",
                "usedPercentage", "threshold", "status");

        // Verify threshold is set to 90%
        assertThat(health.getDetails().get("threshold")).isEqualTo("90%");
    }
}
