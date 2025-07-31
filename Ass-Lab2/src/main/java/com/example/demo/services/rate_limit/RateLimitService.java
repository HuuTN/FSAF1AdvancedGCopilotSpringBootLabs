package com.example.demo.services.rate_limit;

import io.github.bucket4j.Bucket;

public interface RateLimitService {
    Bucket resolveBucket(String key);
}
