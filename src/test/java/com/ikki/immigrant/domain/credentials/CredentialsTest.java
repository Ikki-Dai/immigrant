package com.ikki.immigrant.domain.credentials;

import com.lambdaworks.crypto.SCryptUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CredentialsTest {

    String password = "123456";

    @Test
    public void scryptTest() {
//        long start = System.currentTimeMillis();
        String hash = SCryptUtil.scrypt(password, 16, 80, 28);
//        System.out.println(System.currentTimeMillis() - start);
        Assertions.assertNotNull(hash);
    }

    @Test
    public void verifyTest() {
        Assertions.assertTrue(SCryptUtil.check("123456", "$s0$33208$twECMl2cp1I/zdTRqT2gCg==$Ub2tKqLX0j/Iqutlb9D+XRIDVJy/OLYV0oKwM75r6So="));
    }

}
