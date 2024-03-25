package com.ambidextrous.ratelimiter.interceptor;

import com.ambidextrous.ratelimiter.common.RateLimiterResponse;
import com.ambidextrous.ratelimiter.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {
    private static final int TOO_MANY_REQUESTS = 429;
    RateLimiterService rateLimiterService;

    @Autowired
    public RateLimiterInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String clientIPAddress = request.getHeader("X-FORWARDED-FOR");
        if (clientIPAddress == null)
            clientIPAddress = request.getRemoteAddr();

        RateLimiterResponse rateLimiterResponse = rateLimiterService.isRateLimited(clientIPAddress);

        if (rateLimiterResponse.isRateLimited()) {
            response.setStatus(TOO_MANY_REQUESTS);
            response.getWriter().write(rateLimiterResponse.getMessage());
            response.getWriter().flush();
            return false;
        } else
            return true;
    }

}
