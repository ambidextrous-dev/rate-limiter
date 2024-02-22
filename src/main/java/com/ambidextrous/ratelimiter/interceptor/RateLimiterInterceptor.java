package com.ambidextrous.ratelimiter.interceptor;

import com.ambidextrous.ratelimiter.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final int TOO_MANY_REQUESTS = 429;

    @Autowired
    RateLimiterService rateLimiterService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String clientIPAddress = request.getHeader("X-FORWARDED-FOR");
        if (clientIPAddress == null)
            clientIPAddress = request.getRemoteAddr();

        boolean rateLimited = rateLimiterService.isAllowed(clientIPAddress);

        if (rateLimited) {
            response.setStatus(TOO_MANY_REQUESTS);
            return false;
        } else
            return true;
    }

}
