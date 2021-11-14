package com.ikki.immigrant.application;

import com.ikki.immigrant.application.authentication.CdVO;
import com.ikki.immigrant.application.authentication.CoolDownHandler;
import com.ikki.immigrant.infrastructure.exception.BizException;
import org.junit.jupiter.api.*;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author ikki
 */
@SpringBootTest
@Import(AopAutoConfiguration.class)
public class CoolDownTest {
    private static RedisServer redisServer;
    CoolDownTarget coolDownTarget;

    @Autowired
    CoolDownHandler coolDownHandler;

    @Resource
    RedisTemplate<String, CdVO> redisTemplate;
    String sub = "tomcat@apache.com";

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
    public void beforeEach() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new CoolDownTarget());
        factory.addAspect(coolDownHandler);
        coolDownTarget = factory.getProxy();
    }

    @AfterEach
    public void clearAfter() {
        redisTemplate.delete("COOLDOWN:" + sub);
    }

    @Test
    void loginSuccess() {
        coolDownTarget.loginSuccess(sub);
        Assertions.assertNull(redisTemplate.opsForValue().get("COOLDOWN:" + sub));
    }

    @Test()
    void loginFailed() throws InterruptedException {
        BizException bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("login failed;", bizException.getMessage());
        CdVO obj = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj);
        Assertions.assertEquals(1, obj.getAttempts());

        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("login failed;", bizException.getMessage());
        obj = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj);
        Assertions.assertEquals(2, obj.getAttempts());

        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("login failed;", bizException.getMessage());
        obj = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj);
        Assertions.assertEquals(3, obj.getAttempts());
        //3++
        // 4th retry failed
        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertTrue(bizException.getMessage().startsWith("pls retry after"));
        // 4th retry success
        obj = new CdVO();
        obj.setAttempts(3);
        obj.setLastTime(System.currentTimeMillis() - 60_000L);
        redisTemplate.opsForValue().set("COOLDOWN:" + sub, obj);
        //
        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("login failed;", bizException.getMessage());
        obj = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj);
        Assertions.assertEquals(4, obj.getAttempts());
        // 5th failed
        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertTrue(bizException.getMessage().startsWith("pls retry after"));
        //5th success
        obj = new CdVO();
        obj.setAttempts(4);
        obj.setLastTime(System.currentTimeMillis() - 120_000L);
        redisTemplate.opsForValue().set("COOLDOWN:" + sub, obj);

        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("login failed;", bizException.getMessage());
        obj = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj);
        Assertions.assertEquals(5, obj.getAttempts());

        // 6th try failed
        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertTrue(bizException.getMessage().startsWith("pls retry after"));
        // 6th retry success

        obj = new CdVO();
        obj.setAttempts(5);
        obj.setLastTime(System.currentTimeMillis() - 300_000L);
        redisTemplate.opsForValue().set("COOLDOWN:" + sub, obj);

        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("login failed;", bizException.getMessage());
        obj = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj);
        Assertions.assertEquals(6, obj.getAttempts());

        //
        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(sub));
        Assertions.assertEquals("arrival max attempts, account have been locked", bizException.getMessage());
    }

}
