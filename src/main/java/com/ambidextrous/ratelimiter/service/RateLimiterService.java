package com.ambidextrous.ratelimiter.service;

import com.ambidextrous.ratelimiter.common.RateLimiterResponse;

public interface RateLimiterService {
    RateLimiterResponse isRateLimited(String key);
}
