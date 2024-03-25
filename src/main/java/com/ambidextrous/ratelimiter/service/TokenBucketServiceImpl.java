package com.ambidextrous.ratelimiter.service;

import com.ambidextrous.ratelimiter.common.RateLimiterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final String refillRate;
    private final String bucketPrefix;

    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBucketServiceImpl(RedisTemplate<String, String> redisTemplate,
                                  @Value("${tokenbucket.refill.rate}") String refillRate,
                                  @Value("${tokenbucket.prefix}") String bucketPrefix) {
        this.redisTemplate = redisTemplate;
        this.refillRate = refillRate;
        this.bucketPrefix = bucketPrefix;
    }

    public RateLimiterResponse isRateLimited(String key) {
        String bucketKey = bucketPrefix + key;

        if (redisTemplate.hasKey(bucketKey)) {
            String token = redisTemplate.opsForValue().get(bucketKey);

            if (Integer.parseInt(token) > 0) {
                redisTemplate.opsForValue().decrement(bucketKey);
                return new RateLimiterResponse(false, "OK");
            }
        } else {
            refillBucket(bucketKey);
            redisTemplate.opsForValue().decrement(bucketKey);
            return new RateLimiterResponse(false, "OK");
        }

        return new RateLimiterResponse(true, "Too many requests, please try in a while");
    }

    public void refillBucket(String bucketKey) {
        redisTemplate.opsForValue().set(bucketKey, refillRate);
    }

    //This method runs every minute and refills the bucket
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    private void scheduled_RefillBuckets() {
        Set<String> bucketKeys = redisTemplate.keys(bucketPrefix + "*");
        for (String bucketKey : bucketKeys) {
            refillBucket(bucketKey);
        }
    }

}
