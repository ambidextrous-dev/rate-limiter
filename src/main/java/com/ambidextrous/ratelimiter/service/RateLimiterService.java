package com.ambidextrous.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    RedisTemplate<String, Long> redisTemplate;
    TokenBucketService tokenBucketService;

    @Autowired
    public RateLimiterService(RedisTemplate<String, Long> redisTemplate, TokenBucketService tokenBucketService) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketService = tokenBucketService;
    }

    public boolean isAllowed(String clientIPAddress) {
        return tokenBucketService.getToken(clientIPAddress);
    }

}
