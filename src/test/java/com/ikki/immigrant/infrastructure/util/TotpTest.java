package com.ikki.immigrant.infrastructure.util;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTPGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Base64;

public class TotpTest {

    //=AFIEVOEUWFQHOSQAUHQZ33IZKCXF7YQG
    byte[] secret = Base64.getDecoder().decode("QUZJRVZPRVVXRlFIT1NRQVVIUVozM0laS0NYRjdZUUc=");

    @Test
    void totp() throws URISyntaxException {
        // Generate a secret (or use your own secret)
//        byte[] secret = SecretGenerator.generate();
        System.out.println(Base64.getEncoder().encodeToString(secret));
        TOTPGenerator.Builder builder = new TOTPGenerator.Builder(secret)
                .withPasswordLength(6)
                .withAlgorithm(HMACAlgorithm.SHA1) // SHA256 and SHA512 are also supported
                .withPeriod(Duration.ofSeconds(30));

        TOTPGenerator totp = builder.build();

        String code = totp.generate();
        System.out.println(code);
        URI uri = totp.getURI("immigrant", "tomcat@apache.com");
        System.out.println(uri);
        Assertions.assertEquals(6, code.length());
    }
}
