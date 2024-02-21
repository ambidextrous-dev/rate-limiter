package com.ambidextrous.ratelimiter.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    RedisTemplate redisTemplate;

    @Autowired
    public RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String clientIPAddress) {



        return true;
    }


}
