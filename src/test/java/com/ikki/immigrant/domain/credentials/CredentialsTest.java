package com.ikki.immigrant.domain.credentials;

import com.lambdaworks.crypto.SCryptUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

public class CredentialsTest {

    String password = "123456";

    @RepeatedTest(5)
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    public void scryptTest() {
        long start = System.currentTimeMillis();
        String hash = SCryptUtil.scrypt(password, 16, 80, 28);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(hash);
    }

    @RepeatedTest(5)
    public void verifyTest() {
        Assertions.assertTrue(SCryptUtil.check("123456", "$s0$33208$twECMl2cp1I/zdTRqT2gCg==$Ub2tKqLX0j/Iqutlb9D+XRIDVJy/OLYV0oKwM75r6So="));
    }

}
