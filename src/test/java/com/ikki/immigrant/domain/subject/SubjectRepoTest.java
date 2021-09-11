package com.ikki.immigrant.domain.subject;

import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.EnumSet;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SubjectRepoTest {


    @Autowired
    SubjectRepository subjectRepository;

    @BeforeAll
    public static void init() {
    }

    @BeforeEach
    public void clear() {
        subjectRepository.deleteAll();
        Subject subject = new Subject();
        subject.setUsername("tomcat");
        subject.setEmail("tomcat@apache.com");
        subject.setPhone("131-1234-1235");
        subject.setValid(EnumSet.of(Subject.ValidStatus.PHONE_VERIFIED, Subject.ValidStatus.EMAIL_VERIFIED));
        subject.setAvailable(Subject.AvailableStatus.FREEZE);
        Subject sbj = subjectRepository.save(subject);
        Assertions.assertNotNull(sbj);
    }

    void saveTest() {
        Subject subject = new Subject();
        subject.setUsername("apisix");
        subject.setEmail("apisix@apache.com");
        subject.setPhone("131-1234-1237");
        subject.setValid(EnumSet.of(Subject.ValidStatus.PHONE_VERIFIED, Subject.ValidStatus.EMAIL_VERIFIED));
        subject.setAvailable(Subject.AvailableStatus.NORMAL);
        Subject sbj = subjectRepository.save(subject);
        Optional<Subject> result = subjectRepository.findByEmail("apisix@apache.com");
        System.out.println(result.get());
        Assertions.assertTrue(result.isPresent());
//        Assertions.assertTrue(sbj.getUid() > 0);
    }

    @Test
    void findByPhoneTest() {
        Optional<Subject> result = subjectRepository.findByPhone("131-1234-1235");
        Assertions.assertNotNull(result.get());
    }

    @Test
    void findByEmailTest() {
        Optional<Subject> result = subjectRepository.findByEmail("tomcat@apache.com");
        Assertions.assertNotNull(result.get());
    }

    @Test
    void findAllTest() {
        Iterable<Subject> subjects = subjectRepository.findAll();
        subjects.forEach(System.out::println);
        System.out.println(IterableUtils.size(subjects));
        Assertions.assertTrue(IterableUtils.size(subjects) > 0);
    }


}
