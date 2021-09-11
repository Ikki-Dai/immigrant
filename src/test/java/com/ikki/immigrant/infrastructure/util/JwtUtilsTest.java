package com.ikki.immigrant.infrastructure.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
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
import java.util.*;

public class JwtUtilsTest {

    public static final String SECRET = "this is a test secret need enough length";
    static JWTClaimsSet jwtClaimsSet;
    private final String jwkStr = "{\"kty\":\"OKP\",\"d\":\"dIH-Nmvl9iMU8k1NgVx5W9eNUMFn860ObwUXHg6coLg\",\"use\":\"sig\",\"crv\":\"Ed25519\",\"kid\":\"9a49b214-4384-4144-b793-2c2b5849fbb6\",\"x\":\"p28Ig94jQS9UWDayOYnkeSlUZ7dakikL2Jd0SmtlUUo\"}";
    ECKey ecKey;
    OctetKeyPair octetKeyPair;
    JWSSigner jwsSigner;
    JWSVerifier jwsVerifier;
    private String token;

    @BeforeAll
    public static void beforeAll() {
        Instant issuerTime = Instant.now();
        Instant expireTime = issuerTime.plusSeconds(3600);

        jwtClaimsSet = new JWTClaimsSet.Builder()
                .audience("developer")
                .issuer("random")
                .subject("junit")
                .issueTime(Date.from(issuerTime))
                .expirationTime(Date.from(expireTime))
                .build();
    }

    @BeforeEach
    public void prepareKey() throws JOSEException {
        // generate EC key
        ECKeyGenerator ecKeyGenerator = new ECKeyGenerator(Curve.P_256);
        ecKey = ecKeyGenerator.generate();
        // generate ed25519
        OctetKeyPairGenerator octetKeyPairGenerator = new OctetKeyPairGenerator(Curve.Ed25519);
        octetKeyPair = octetKeyPairGenerator.generate();

    }

    @Test
    void testEC() throws JOSEException, ParseException {
        jwsSigner = new ECDSASigner(ecKey);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.ES256), jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        token = signedJWT.serialize();
        Assertions.assertNotNull(token);

        jwsVerifier = new ECDSAVerifier(ecKey.toECPublicKey());
        SignedJWT jwt = SignedJWT.parse(token);
        Assertions.assertTrue(jwt.verify(jwsVerifier));
    }

    @Test
    void ed25519Test() throws JOSEException, ParseException {
        jwsSigner = new Ed25519Signer(octetKeyPair);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.EdDSA), jwtClaimsSet);
        signedJWT.sign(jwsSigner);

        token = signedJWT.serialize();
        System.out.println(token);

        jwsVerifier = new Ed25519Verifier(octetKeyPair.toPublicJWK());
        SignedJWT jwt = SignedJWT.parse(token);
        Assertions.assertTrue(jwt.verify(jwsVerifier));
    }


    @Test
    void test() {
        Set<Curve> sets = OctetKeyPairGenerator.SUPPORTED_CURVES;
        sets.forEach(System.out::println);
        Assertions.assertNotNull(sets);
    }

    @Test
    void buildJwt() throws JOSEException {
        jwsSigner = new MACSigner(SECRET);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        token = signedJWT.serialize();
        System.out.println(token);
        Assertions.assertNotNull(token);
    }

    /**
     * does not support @ java.security.Key
     *
     * @throws ParseException
     * @throws BadJOSEException
     * @throws JOSEException
     * @see com.nimbusds.jose.proc.JWSKeySelector
     */
    void verifyProcessor() throws ParseException, BadJOSEException, JOSEException {
        OctetKeyPair octetKeyPair = OctetKeyPair.parse(jwkStr);

        // generate token wait to verify
        jwsSigner = new Ed25519Signer(octetKeyPair);
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.EdDSA), jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        token = signedJWT.serialize();

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
                new DefaultJWTProcessor<>();

        ImmutableJWKSet<SecurityContext> keySet = new ImmutableJWKSet<>(new JWKSet(octetKeyPair));

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.EdDSA, keySet);

        jwtProcessor.setJWSKeySelector(keySelector);

        List<JWK> matches = new JWKSelector(
                new JWKMatcher.Builder()
                        .keyType(KeyType.OKP)
//                        .keyID("123456")
                        .build())
                .select(new JWKSet(octetKeyPair));
        System.out.println(matches.size());

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                Collections.singleton("developer"),
                new JWTClaimsSet.Builder().issuer("random").build(),
                new HashSet<>(Arrays.asList("sub", "iat", "exp")),
                null));

        // JwtProcess does not support okp type Key
        // java  key is not support
        JWTClaimsSet claimsSet = jwtProcessor.process(token, null);


        System.out.println(claimsSet.toJSONObject());
    }


    @Test
    void parse() throws ParseException, JOSEException {
        OctetKeyPair octetKeyPair = OctetKeyPair.parse(jwkStr);
        Base64URL base64URL = octetKeyPair.computeThumbprint();

        System.out.println(octetKeyPair.toPublicJWK().toJSONString());
        System.out.println(octetKeyPair.toJSONString());

        System.out.println(base64URL);

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
