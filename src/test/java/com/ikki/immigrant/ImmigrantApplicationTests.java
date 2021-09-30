package com.ikki.immigrant;

import com.ikki.immigrant.infrastructure.advice.BizExceptionAdvice;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import java.io.IOException;

@SpringBootTest
@Import(BizExceptionAdvice.class)
class ImmigrantApplicationTests {

    private static RedisServer redisServer;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    RedisTemplate redisTemplate;

    @BeforeAll
    public static void setupRedis() throws IOException {
        redisServer = RedisServer.builder().port(6379).setting("maxmemory 128M").build();
        redisServer.start();
    }

    @AfterAll
    public static void tearDownRedis() throws IOException {
        redisServer.stop();
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(applicationContext);
    }

    @Test
    void redisServer() {
        Assertions.assertNotNull(redisTemplate);
    }
}
