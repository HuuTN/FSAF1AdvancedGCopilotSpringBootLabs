package com.example.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ActuatorHealthEndpointIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpointShouldReturnMemoryHealthIndicator() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).isNotNull();

        // Verify the custom memory health indicator is included
        assertThat(body).contains("\"maxMemory\":{\"status\"");
        assertThat(body).contains("\"maxMemory\":\"");
        assertThat(body).contains("usedMemory");
        assertThat(body).contains("usedPercentage");
        assertThat(body).contains("threshold");

        // Verify disk space health indicator is included
        assertThat(body).contains("diskSpace");

        // Verify overall health status
        assertThat(body).containsAnyOf("\"status\":\"UP\"", "\"status\":\"DOWN\"");
    }
}
