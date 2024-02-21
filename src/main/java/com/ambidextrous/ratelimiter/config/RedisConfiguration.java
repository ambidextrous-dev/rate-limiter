package com.ambidextrous.ratelimiter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String hostName;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(this.hostName);
        jedisConnectionFactory.setPort(this.port);
        return jedisConnectionFactory;
    }

    @Bean
    RedisTemplate<String,String> redisTemplate() {
        RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

}
