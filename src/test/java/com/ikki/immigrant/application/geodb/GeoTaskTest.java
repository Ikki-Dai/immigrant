package com.ikki.immigrant.application.geodb;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * @author ikki
 */
@SpringBootTest
class GeoTaskTest {

    private static RedisServer redisServer;

    @Autowired
    GeoDBUpdateTask geoDBUpdateTask;

    @BeforeAll
    public static void setupRedis() throws IOException {
        redisServer = RedisServer.builder().port(6379).setting("maxmemory 128M").build();
        redisServer.start();
    }

    @AfterAll
    public static void tearDownRedis() throws IOException {
        redisServer.stop();
    }

    @BeforeEach
    void beforeEach() {
        String dbPath = ReflectionTestUtils.getField(geoDBUpdateTask, "dbPath").toString();
        System.out.println(dbPath);
        Assertions.assertNotNull(geoDBUpdateTask);
    }

    @Test
    void init() {
        if (GeoDBUpdateTask.ready) {
            Assertions.assertTrue(geoDBUpdateTask.dbFileExist());
        }
    }


}
