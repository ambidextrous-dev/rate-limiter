package com.ambidextrous.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    RedisTemplate<String, String> redisTemplate;
    TokenBucketService tokenBucketService;

    @Autowired
    public RateLimiterService(RedisTemplate<String, String> redisTemplate, TokenBucketService tokenBucketService) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketService = tokenBucketService;
    }

    public boolean isRateLimited(String clientIPAddress) {
        return tokenBucketService.isRateLimited(clientIPAddress);
    }

}
