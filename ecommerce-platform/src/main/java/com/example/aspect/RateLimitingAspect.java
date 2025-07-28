package com.example.aspect;

import com.example.annotation.RateLimited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Aspect for handling rate limiting on API endpoints.
 * Implements a simple in-memory rate limiting mechanism.
 */
@Aspect
@Component
public class RateLimitingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingAspect.class);

    // In-memory store for rate limiting (use Redis in production)
    private final ConcurrentMap<String, RequestInfo> requestCache = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String clientId = getClientIdentifier();
        String key = clientId + ":" + joinPoint.getSignature().getName();

        RequestInfo requestInfo = requestCache.computeIfAbsent(key, k -> new RequestInfo());

        synchronized (requestInfo) {
            LocalDateTime now = LocalDateTime.now();
            long periodInSeconds = parsePeriod(rateLimited.period());

            // Clean old requests outside the time window
            requestInfo.cleanOldRequests(now, periodInSeconds);

            // Check if rate limit is exceeded
            if (requestInfo.getRequestCount() >= rateLimited.requests()) {
                logger.warn("Rate limit exceeded for client {} on endpoint {}", clientId,
                        joinPoint.getSignature().getName());
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, rateLimited.message());
            }

            // Add current request
            requestInfo.addRequest(now);
            logger.debug("Request allowed for client {} on endpoint {}. Count: {}/{}",
                    clientId, joinPoint.getSignature().getName(),
                    requestInfo.getRequestCount(), rateLimited.requests());
        }

        return joinPoint.proceed();
    }

    private String getClientIdentifier() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // Use IP address as client identifier (in production, consider using
                // authenticated user ID)
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.warn("Could not determine client identifier: {}", e.getMessage());
        }
        return "unknown";
    }

    private long parsePeriod(String period) {
        if (period.endsWith("s")) {
            return Long.parseLong(period.substring(0, period.length() - 1));
        } else if (period.endsWith("m")) {
            return Long.parseLong(period.substring(0, period.length() - 1)) * 60;
        } else if (period.endsWith("h")) {
            return Long.parseLong(period.substring(0, period.length() - 1)) * 3600;
        } else if (period.endsWith("d")) {
            return Long.parseLong(period.substring(0, period.length() - 1)) * 86400;
        } else {
            // Default to seconds
            return Long.parseLong(period);
        }
    }

    /**
     * Helper class to track request information for rate limiting.
     */
    private static class RequestInfo {
        private final ConcurrentMap<LocalDateTime, Integer> requests = new ConcurrentHashMap<>();

        public void addRequest(LocalDateTime timestamp) {
            requests.put(timestamp, 1);
        }

        public int getRequestCount() {
            return requests.size();
        }

        public void cleanOldRequests(LocalDateTime now, long periodInSeconds) {
            LocalDateTime cutoff = now.minus(periodInSeconds, ChronoUnit.SECONDS);
            requests.entrySet().removeIf(entry -> entry.getKey().isBefore(cutoff));
        }
    }
}
