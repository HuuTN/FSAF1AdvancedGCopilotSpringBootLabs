package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for rate limiting API endpoints.
 * Limits the number of requests a client can make within a specified time
 * period.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /**
     * Number of requests allowed within the time period.
     * 
     * @return maximum number of requests
     */
    int requests() default 100;

    /**
     * Time period for rate limiting.
     * Supported formats: "1s", "1m", "1h", "1d"
     * 
     * @return time period string
     */
    String period() default "1m";

    /**
     * Error message to return when rate limit is exceeded.
     * 
     * @return error message
     */
    String message() default "Rate limit exceeded. Please try again later.";
}
