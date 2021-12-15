package com.ikki.immigrant.domain.credentials;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CredentialsTest {

    @Test
    void test() {
        Credentials credentials = new Credentials();
        Assertions.assertNotNull(credentials);
    }

}
