package com.ambidextrous.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@ConditionalOnProperty(value = "rate.limiter.algorithm", havingValue = "tokenbucket")
public class TokenBucketServiceImpl implements RateLimiterService {
    private final String REFILL_RATE = "3"; //requests per minute
    private final String BUCKET_PREFIX = "token_bucket";

    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBucketServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isRateLimited(String key) {
        String bucketKey = BUCKET_PREFIX + key;

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

    public void refillBucket(String bucketKey) {
        redisTemplate.opsForValue().set(bucketKey, REFILL_RATE);
    }

    //This method runs every minute and refills the bucket
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    private void scheduled_RefillBuckets() {
        Set<String> bucketKeys = redisTemplate.keys(BUCKET_PREFIX + "*");
        for (String bucketKey : bucketKeys) {
            refillBucket(bucketKey);
        }
    }

}
