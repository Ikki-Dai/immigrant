package com.ikki.immigrant.infrastructure.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@AutoConfigureBefore(RedisTemplate.class)
@Component
public class EmbeddedRedisServer {

    private static RedisServer redisServer;

    @PostConstruct
    public RedisServer setup() {
        redisServer = RedisServer.builder().port(6379).setting("maxmemory 128M").build();
        redisServer.start();
        return redisServer;
    }

    @PreDestroy
    public void destroy() {
        redisServer.stop();
    }
}
