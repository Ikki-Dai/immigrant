package com.ikki.immigrant.domain.subject;

import com.ikki.immigrant.infrastructure.advice.BizExceptionAdvice;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

@SpringBootTest
@Import(BizExceptionAdvice.class)
class SubjectServiceTest {
    private static RedisServer redisServer;
    @Autowired
    SubjectService subjectService;
    @Autowired
    SubjectRepository subjectRepository;

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
    void clear() {
        subjectRepository.deleteAll();
        Subject subject = new Subject();
        subject.setUsername("tomcat");
        subject.setEmail("tomcat@apache.com");
        subject.setPhone("+8613112341235");
        subject.setValid(EnumSet.of(Subject.ValidStatus.PHONE_VERIFIED, Subject.ValidStatus.EMAIL_VERIFIED));
        subject.setAvailable(Subject.AvailableStatus.FREEZE);
        Subject sbj = subjectRepository.save(subject);
        Assertions.assertNotNull(sbj);
    }

    @ParameterizedTest
    @CsvSource({
            "tomcat, true",
            "tomcat@apache.com, true",
            "+8613112341235, true",
            "+, false"
    })
    void testExist(String subject, boolean exist) {
        Optional<Subject> optSub = subjectService.checkExist(subject);
        Assertions.assertFalse(optSub.isPresent() ^ exist);
    }


    @ParameterizedTest
    @CsvSource({
            "apisix, true",
            "apisix@apache.com, true",
            "+13372464366, true",
            "+, false"
    })
    void signupTest(String subject, boolean exist) {
        Optional<Subject> subObj = subjectService.signup(subject);
        subObj.ifPresent(System.out::println);
        Assertions.assertFalse(subObj.isPresent() ^ exist);

    }


}
