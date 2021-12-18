package com.ikki.immigrant.application.geodb;

import com.maxmind.geoip2.model.CityResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * @author ikki
 */
@SpringBootTest
class GeoDBServiceTest {

    private static RedisServer redisServer;

    @Autowired
    GeoDBService geoDBService;
    String ipv4 = "222.65.182.172";
    String ipv6 = "2600:3c01::f03c:92ff:fe4a:8a1e";

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
    void queryTest() {

        CityResponse cityResponse = geoDBService.queryCityByIp(ipv6);
        Assertions.assertNotNull(cityResponse);
    }

    @Test
    void queryByLocale() {
        GeoLocation geoLocation = geoDBService.queryByLocale(ipv6, "en");
        Assertions.assertNotNull(geoLocation);
    }

}
