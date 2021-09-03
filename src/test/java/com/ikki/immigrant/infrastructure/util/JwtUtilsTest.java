package com.ikki.immigrant.infrastructure.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JwtUtilsTest {

    public static final String SECRET = "this is a test secret need enough length";
    static ECKey ecKey;
    private final String jwkStr = "{\"kty\":\"OKP\",\"d\":\"dIH-Nmvl9iMU8k1NgVx5W9eNUMFn860ObwUXHg6coLg\",\"use\":\"sig\",\"crv\":\"Ed25519\",\"kid\":\"9a49b214-4384-4144-b793-2c2b5849fbb6\",\"x\":\"p28Ig94jQS9UWDayOYnkeSlUZ7dakikL2Jd0SmtlUUo\"}";
    JWSSigner jwsSigner;
    JWSVerifier jwsVerifier;
    JWTClaimsSet jwtClaimsSet;
    private String token;

    @BeforeAll
    public static void beforeAll() throws JOSEException {
        ECKeyGenerator ecKeyGenerator = new ECKeyGenerator(Curve.P_256);
        ecKey = ecKeyGenerator.generate();

//        System.out.println(ecKey.toPublicJWK().toJSONString());
//        System.out.println(ecKey.toJSONString());

    }

    @BeforeEach
    public void prepareClaims() {
        Instant issuerTime = Instant.now();
        Instant expireTime = issuerTime.plusSeconds(3600);

        jwtClaimsSet = new JWTClaimsSet.Builder()
                .audience("developer")
                .issuer("developer")
                .subject("junit")
                .issueTime(Date.from(issuerTime))
                .expirationTime(Date.from(expireTime))
                .build();
    }

    @Test
    public void testEC() throws JOSEException, ParseException {
        jwsSigner = new ECDSASigner(ecKey);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.ES256), jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        token = signedJWT.serialize();
        System.out.println(token);


        jwsVerifier = new ECDSAVerifier(ecKey);
        SignedJWT jwt = SignedJWT.parse(token);
        Assertions.assertTrue(jwt.verify(jwsVerifier));
    }

    @Test
    public void test() {
        Set<Curve> sets = OctetKeyPairGenerator.SUPPORTED_CURVES;
        sets.forEach(System.out::println);
    }

    @Test
    public void buildJwt() throws JOSEException {
        jwsSigner = new MACSigner(SECRET);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        token = signedJWT.serialize();
        System.out.println(token);
    }

    @Test
    public void verifyToken() throws ParseException, JOSEException {
        jwsVerifier = new MACVerifier(SECRET);
        SignedJWT jwt = SignedJWT.parse(token);
        Assertions.assertTrue(jwt.verify(jwsVerifier));
    }

    @Test
    public void verifyProcessor() throws ParseException, BadJOSEException, JOSEException {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFZERTQSJ9.eyJpc3MiOiJkZXZlbG9wZXIiLCJhdWQiOiJkZXZlbG9wZXIiLCJzdWIiOiJqdW5pdCIsImV4cCI6MTYyODQxMDMzNSwiaWF0IjoxNjI4NDA2NzM1fQ.nhRtb11SG9_4mtXC-jEqQ6_mf7_6wy64WWoOUa1sBQpKW4b-RTYRPgNyK06OyU8_lG5YiVlj2XOZvzzGeBPrAg";

        OctetKeyPair octetKeyPair = OctetKeyPair.parse(jwkStr);

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
                new DefaultJWTProcessor<>();

        JWKSource<SecurityContext> keySource =
                new ImmutableJWKSet<>(new JWKSet(octetKeyPair));

        JWSKeySelector<SecurityContext> keySelector =
//                new JWSVerificationKeySelector<SecurityContext>(JWSAlgorithm.EdDSA, keySource);
//                new SingleKeyJWSKeySelector<SecurityContext>(JWSAlgorithm.EdDSA, keySource);
                new JWSVerificationKeySelector<>(JWSAlgorithm.EdDSA, keySource);


        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder()
                .keyType(KeyType.OKP)
                .algorithm(JWSAlgorithm.EdDSA)
                .build());


        jwtProcessor.setJWSKeySelector(keySelector);


        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier(
                null,
                new JWTClaimsSet.Builder().issuer("developer").build(),
                new HashSet<>(Arrays.asList("sub", "iat", "exp")),
                null));

        JWTClaimsSet claimsSet = jwtProcessor.process(token, null);

        System.out.println(claimsSet.toJSONObject());
    }


    @Test
    public void parse() throws ParseException, JOSEException {
//        ECKey ecKey = ECKey.parse(jwkStr);
        OctetKeyPair octetKeyPair = OctetKeyPair.parse(jwkStr);
        Base64URL base64URL = octetKeyPair.computeThumbprint();

        System.out.println(octetKeyPair.toPublicJWK().toJSONString());
        System.out.println(octetKeyPair.toJSONString());

        System.out.println(base64URL);
//        byte[] publicBytes = octetKeyPair.toPublicKey().getEncoded();
//        byte[] privateBytes = octetKeyPair.toPrivateKey().getEncoded();
//        System.out.println(Base64.getEncoder().encodeToString(publicBytes));
//        System.out.println(Base64.getEncoder().encodeToString(privateBytes));
        JWSSigner jwsSigner = new Ed25519Signer(octetKeyPair);

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                        .type(JOSEObjectType.JWT)
                        .build(),
                jwtClaimsSet
        );

        signedJWT.sign(jwsSigner);

        System.out.println(signedJWT.serialize());

        SignedJWT s2 = SignedJWT.parse(signedJWT.serialize());

        JWSVerifier jwsVerifier = new Ed25519Verifier(octetKeyPair.toPublicJWK());

        Assertions.assertTrue(s2.verify(jwsVerifier));

    }

}
