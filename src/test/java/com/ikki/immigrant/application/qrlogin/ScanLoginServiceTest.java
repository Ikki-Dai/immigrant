package com.ikki.immigrant.application.qrlogin;

import org.junit.jupiter.api.*;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.UUID;

/**
 * @author ikki
 */
@SpringBootTest
class ScanLoginServiceTest {
    private static RedisServer redisServer;

    @SpyBean
    ScanLoginService scanLoginService;

    @Autowired
    RedissonClient redissonClient;

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
    void initCheck() {

        String brokerId = (String) ReflectionTestUtils.getField(scanLoginService, "brokerId");
        Assertions.assertEquals(10, brokerId.length());

        String channel = (String) ReflectionTestUtils.getField(scanLoginService, "channel");
        Assertions.assertEquals("SSE-" + brokerId, channel);


        Assertions.assertNotNull(scanLoginService);
    }

    @Test
    void connectTest() {
        String clientId = UUID.randomUUID().toString();
        SseEmitter sseEmitter = scanLoginService.connect(clientId);
        Assertions.assertNotNull(sseEmitter);
    }

    @Test
    void reconnectTest() {
        String clientId = UUID.randomUUID().toString();
        SseEmitter sseEmitter = scanLoginService.reconnect(clientId, 0L);
        Assertions.assertNotNull(sseEmitter);
    }

    /**
     * need higher redis version support reliable topic
     */
    @Test
    void publishTest() {
        String clientId = UUID.randomUUID().toString();
        SseEmitter sseEmitter = scanLoginService.connect(clientId);
        SseValueObject sseValueObject = new SseValueObject();
        sseValueObject.setClientId(clientId);
        sseValueObject.setName(SseValueObject.EventName.CONFIRM);
        sseValueObject.setId(String.valueOf(System.currentTimeMillis()));
        sseValueObject.setData("user xxx confirm login");

        scanLoginService.publish(clientId, sseValueObject);

    }

}
