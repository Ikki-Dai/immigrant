package com.ikki.immigrant.domain.credentials;

import com.ikki.immigrant.infrastructure.config.DataJdbcConfiguration;
import com.lambdaworks.crypto.SCryptUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJdbcTest
@Import(DataJdbcConfiguration.class)
public class CredentialsRepoTest {

    @Autowired
    CredentialsRepository credentialsRepository;

    @BeforeEach
    void prepare() {
        Credentials credentials = new Credentials();
        credentials.setUid(12345L);
        credentials.setCredential(SCryptUtil.scrypt("123456", 16, 80, 28));
        credentials.setAlias("");
        credentials.setIdentifier("");
        credentials.setType(Credentials.Type.PASSWORD);
        credentialsRepository.save(credentials);
    }

    @Test
    void queryTest() {
        Optional<Credentials> credentials = credentialsRepository.findByUid(12345L);
        Assertions.assertTrue(credentials.isPresent());
        System.out.println(credentials.get());
    }
}
