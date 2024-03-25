package com.ambidextrous.ratelimiter.service;

import com.ambidextrous.ratelimiter.common.RateLimiterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "rate.limiter.algorithm", havingValue = "fixedwindow")
public class FixedWindowCounterServiceImpl implements RateLimiterService {
    private final Long windowSizeSeconds;
    private final int windowThreshold;
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public FixedWindowCounterServiceImpl(RedisTemplate<String, String> redisTemplate,
                                         @Value("${fixedwindow.windowsize.seconds}") Long windowSizeSeconds,
                                         @Value("${fixedwindow.window.threshold}") int windowThreshold) {
        this.redisTemplate = redisTemplate;
        this.windowSizeSeconds = windowSizeSeconds;
        this.windowThreshold = windowThreshold;
    }

    @Override
    public RateLimiterResponse isRateLimited(String key) {
        //get current timestamp and check the counter for the current window
        //if not possible block and send the next window time back again
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        String timeWindow = String.valueOf(currentTimeInSeconds - (currentTimeInSeconds % windowSizeSeconds));

        if (!redisTemplate.hasKey(timeWindow)) {
            redisTemplate.opsForValue().set(timeWindow, "1");
            return new RateLimiterResponse(false, "OK");
        } else if (Integer.parseInt(redisTemplate.opsForValue().get(timeWindow)) < windowThreshold) {
            redisTemplate.opsForValue().increment(timeWindow);
            return new RateLimiterResponse(false, "OK");
        } else {
            return new RateLimiterResponse(true, "Too many requests, please try in " + windowSizeSeconds + " seconds");
        }
    }

}
