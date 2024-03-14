package com.ambidextrous.ratelimiter.service;

public interface RateLimiterService {
    boolean isRateLimited(String key);
}
