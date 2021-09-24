package com.ikki.immigrant.infrastructure.util;

import com.password4j.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Password4jTest {

    private static final String PASSWORD = "passw0rd";

    @Test
    void pbkdfTest() {
        PBKDF2Function pbkdf2 = AlgorithmFinder.getPBKDF2Instance();
        Hash hash = Password.hash(PASSWORD).with(pbkdf2);
//        System.out.println(hash.getPepper());
        System.out.println(hash.getResult());
        System.out.println(hash.getSalt());

        boolean b = Password.check(PASSWORD, hash.getResult())
                .addSalt(hash.getSalt())
                .with(pbkdf2);
        Assertions.assertTrue(b);


    }

    @Test
    void bcryptTest() {
        BCryptFunction bCrypt = AlgorithmFinder.getBCryptInstance();
        Hash hash = Password.hash(PASSWORD).with(bCrypt);
//        System.out.println(hash.getPepper());
        System.out.println(hash.getResult());
        System.out.println(hash.getSalt());

        boolean b = Password.check(PASSWORD, hash.getResult())
//                .addSalt(hash.getSalt())
                .with(bCrypt);
        Assertions.assertTrue(b);

    }

    @Test
    void scryptTest() {
        SCryptFunction sCrypt = AlgorithmFinder.getSCryptInstance();
        Hash hash = Password.hash(PASSWORD).with(sCrypt);
//        System.out.println(hash.getPepper());
        System.out.println(hash.getResult());
        System.out.println(hash.getSalt());

        boolean b = Password.check(PASSWORD, hash.getResult())
                .addSalt(hash.getSalt())
                .with(sCrypt);
        Assertions.assertTrue(b);

    }

    @Test
    void argon2Test() {
        String hashed = "$argon2id$v=19$m=1024,t=3,p=12$MzMzMzMzMzM$DXUE5N4lm4plldg9nGMq+tYsbGhko8HWpPaADujpgFQ";
        Argon2Function argon2 = Argon2Function.getInstanceFromHash(hashed);
        Hash hash = Password.hash(PASSWORD).with(argon2);
        System.out.println(hash.getResult());
        System.out.println(hash.getSalt());

        boolean b = Password.check(PASSWORD, hash.getResult())
                .addSalt(hash.getSalt())
                .with(argon2);
        Assertions.assertTrue(b);

    }
}
