package com.ikki.immigrant.application.authentication;

import com.ikki.immigrant.infrastructure.exception.BizException;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        coolDownTarget.loginSuccess(RandomString.make(5), sub, RandomString.make(5));
        Assertions.assertNull(redisTemplate.opsForValue().get("COOLDOWN:" + sub));
    }

    @ParameterizedTest
    @CsvSource({
            "1, 0, failed",
            "2, 0, failed",
            "3, 60, retry",
            "4, 120, retry",
            "5, 300, retry",
            "6, 86400, lock"})
    void loginFailed(int times, long wait, String msg) throws InterruptedException {
        //prepare
        CdVO obj = new CdVO();
        obj.setAttempts(times);

        // wait no enough time
        obj.setLastTime(System.currentTimeMillis() - Math.abs(wait * 1000L - 5_000L));
        redisTemplate.opsForValue().set("COOLDOWN:" + sub, obj);

        BizException bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(RandomString.make(5), sub, RandomString.make(5)));
        Assertions.assertTrue(bizException.getMessage().contains(msg));
        CdVO obj2 = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj2);


        // wait enough time
        obj.setLastTime(System.currentTimeMillis() - (wait * 1000L + 5_000L));
        redisTemplate.opsForValue().set("COOLDOWN:" + sub, obj);

        bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(RandomString.make(5), sub, RandomString.make(5)));
        Assertions.assertEquals("login failed;", bizException.getMessage());

        obj2 = redisTemplate.opsForValue().get("COOLDOWN:" + sub);
        System.out.println(obj2);

    }

    @Test
    void loginAfterExpire() {
        CdVO obj = new CdVO();
        obj.setAttempts(6);
        obj.setLastTime(System.currentTimeMillis() - 86400_000L - 5_000L);
        redisTemplate.opsForValue().set("COOLDOWN:" + sub, obj);

        BizException bizException = Assertions.assertThrows(BizException.class, () -> coolDownTarget.loginFailed(RandomString.make(5), sub, RandomString.make(5)));
        Assertions.assertEquals("login failed;", bizException.getMessage());
    }

}
