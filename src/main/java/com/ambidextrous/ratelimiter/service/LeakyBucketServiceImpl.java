package com.ambidextrous.ratelimiter.service;

import com.ambidextrous.ratelimiter.common.RateLimiterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@ConditionalOnProperty(value = "rate.limiter.algorithm", havingValue = "leakybucket")
public class LeakyBucketServiceImpl implements RateLimiterService {
    private final int queueSize;
    private final String bucketPrefix;
    private Queue<String> requestQueue;
    private final int LEAK_RATE = 10; //API limit of 10 per hour

    @Autowired
    public LeakyBucketServiceImpl(@Value("${leakybucket.queue.size}") int queueSize,
                                  @Value("${leakybucket.prefix}") String bucketPrefix) {
        this.queueSize = queueSize;
        this.bucketPrefix = bucketPrefix;
        this.requestQueue = new LinkedBlockingQueue<>(queueSize);
    }

    @Override
    public RateLimiterResponse isRateLimited(String key) {
        boolean isRateLimited = !requestQueue.offer(bucketPrefix + key);

        if (isRateLimited)
            return new RateLimiterResponse(true, "Too many requests, please try in a while");
        else
            return new RateLimiterResponse(false, "OK");
    }

    //Runs every few minutes based on the leak rate per hour
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 60 / LEAK_RATE)
    private void processQueueRequests() {
        if (!requestQueue.isEmpty())
            requestQueue.poll();
    }
}
