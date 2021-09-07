package com.ikki.immigrant.domain.subject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
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
//        subjectCredentialsRepository.deleteAll();
    }

    @RepeatedTest(1)
    public void saveTest() {
//        subjectCredentialsRepository.deleteAll();
        Subject subject = new Subject();
        subject.setUsername("tomcat");
        subject.setEmail("tomcat@apache.com");
        subject.setPhone("131-1234-1235");
        subject.setValid(EnumSet.of(Subject.ValidStatus.EMAIL_VERIFIED));
        subject.setAvailable(Subject.UsableStatus.NORMAL);
        Subject credentials = subjectRepository.save(subject);
//        System.out.println(credentials.getId());
//        Assertions.assertNotNull(credentials);

        Optional<Subject> result = subjectRepository.findById(640318559725027328L);
        result.ifPresent(System.out::println);
//        Optional<SubjectCredentials> result = subjectCredentialsRepository.findByPhone("131-xxxx-xxxx");
//        Assertions.assertNotNull(result.get());
    }

    public void queryTest() {
        Optional<Subject> result = subjectRepository.findByPhone("131-1234-1235");
        Assertions.assertNotNull(result.get());
    }

    public void queryTest2() {
        Iterable<Subject> credentials = subjectRepository.findAll();
        credentials.forEach(System.out::println);
    }


}
