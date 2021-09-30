package com.ikki.immigrant.infrastructure.util;

import com.ikki.immigrant.infrastructure.advice.BizExceptionAdvice;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

@SpringBootTest
@Import({RedissonAutoConfiguration.class, BizExceptionAdvice.class})
@Slf4j
public class RedissonTest {

    private static final int insertions = 1_00;
    private static RedisServer redisServer;
    @Autowired
    RedissonClient redisson;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

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
    void prepare() {
//        redisServer = RedisServer.builder().port(6379).setting("maxmemory 128M").build();
//        redisServer.start();
        Assertions.assertNotNull(redisTemplate);
        redisTemplate.delete("{subject}:config");
        redisTemplate.delete("subject");
        Assertions.assertNotNull(redisson);
//        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("subject");
//        bloomFilter.clearExpire();
    }

    @AfterEach
    void destroy() {
//        redisServer.stop();
    }

    @Test
    void RBloomFilterTest() {
        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("subject", new KryoCodec());
        bloomFilter.tryInit(insertions, 0.01);
        String subject;

        Set<String> subSet = new HashSet<>(insertions);
        LongAdder l1 = new LongAdder();
        for (int i = 0; i < insertions; i++) {
            subject = RandomString.make();
//            // if not exist
            if (!bloomFilter.contains(subject)) {
                subSet.add(subject);
                bloomFilter.add(subject);

            } else {
                // if exist
                // fake
                if (!subSet.contains(subject)) {
                    subSet.add(subject);
                    bloomFilter.add(subject);
                    l1.increment();
                }
            }
            if (i % 1_0000 == 0) {
                log.info("{} inserted", i);
            }

        }
        long start = System.currentTimeMillis();
        subject = RandomString.make();
        System.out.println(bloomFilter.contains(subject));
        long end = System.currentTimeMillis();
        System.out.println("contains cost:" + (end - start));

        start = System.currentTimeMillis();
        subject = RandomString.make();
        System.out.println(bloomFilter.add(subject));
        end = System.currentTimeMillis();
        System.out.println("add cost:" + (end - start));


        System.out.println(bloomFilter.count());
        System.out.println(bloomFilter.getSize());
        System.out.println(subSet.size());
        System.out.println(l1.intValue());
        Assertions.assertTrue((l1.intValue() / subSet.size()) < 0.01);
    }

    @Test
    void IdGenerateTest() {
        RIdGenerator rIdGenerator = redisson.getIdGenerator("uid");
        rIdGenerator.tryInit(insertions, 20);
        Assertions.assertEquals(insertions, rIdGenerator.nextId());
    }

}
