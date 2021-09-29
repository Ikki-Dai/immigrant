package com.ikki.immigrant.domain.subject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.EnumSet;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SubjectServiceTest {

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectRepository subjectRepository;

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
