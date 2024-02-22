package com.ambidextrous.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class TokenBucketService {
    private static final String REFILL_RATE = "3"; //requests per minute
    static final String BUCKET_PREFIX = "token_bucket";

    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBucketService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void refillBucket(String bucketKey) {
        redisTemplate.opsForValue().set(bucketKey, REFILL_RATE);
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    private void scheduled_RefillBuckets() {
        Set<String> bucketKeys = redisTemplate.keys(BUCKET_PREFIX+"*");
        for (String bucketKey : bucketKeys) {
            refillBucket(bucketKey);
        }
    }

    public boolean isRateLimited(String ipAddress) {
        String bucketKey = BUCKET_PREFIX + ipAddress;

        if (redisTemplate.hasKey(bucketKey)) {
            String token = redisTemplate.opsForValue().get(bucketKey);

            if (Integer.parseInt(token) > 0) {
                redisTemplate.opsForValue().decrement(bucketKey);
                return false;
            }
        } else {
            refillBucket(bucketKey);
            redisTemplate.opsForValue().decrement(bucketKey);
            return false;
        }

        return true;
    }

}
