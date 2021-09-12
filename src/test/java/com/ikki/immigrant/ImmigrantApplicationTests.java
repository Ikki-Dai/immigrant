package com.ikki.immigrant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ImmigrantApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(applicationContext);
    }

    @Test
    void redisServer() {
        Assertions.assertNotNull(redisTemplate);
    }
}
