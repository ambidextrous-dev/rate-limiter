package com.ambidextrous.ratelimiter.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RateLimiterResponse {
    @Getter
    boolean isRateLimited;

    @Getter
    String message;
}
