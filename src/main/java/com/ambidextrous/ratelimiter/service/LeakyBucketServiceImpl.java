package com.ambidextrous.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final int QUEUE_SIZE = 10;
    private final int LEAK_RATE = 10; //API limit of 10 per hour
    private final String BUCKET_PREFIX = "token_bucket";
    private Queue<String> requestQueue;

    @Autowired
    public LeakyBucketServiceImpl() {
        this.requestQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    }

    @Override
    public boolean isRateLimited(String key) {
        return !requestQueue.offer(BUCKET_PREFIX + key);
    }

    //Runs every few minutes based on the leak rate per hour
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 60 / LEAK_RATE)
    private void processQueueRequests() {
        if (!requestQueue.isEmpty())
            requestQueue.poll();
    }
}
