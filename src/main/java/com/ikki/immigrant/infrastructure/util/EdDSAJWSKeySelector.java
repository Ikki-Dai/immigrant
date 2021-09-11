package com.ikki.immigrant.infrastructure.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.Key;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @param <C>
 * @see com.nimbusds.jose.proc.JWSKeySelector which does not support OctKeyPair
 * @see com.nimbusds.jose.jwk.OctetKeyPair
 * @deprecated
 */
@Deprecated
public class EdDSAJWSKeySelector<C extends SecurityContext> implements JWSKeySelector<C> {

    private final JWKSource<C> jwkSource;

    JWSAlgorithm jwsAlgorithm = JWSAlgorithm.EdDSA;

    public EdDSAJWSKeySelector(JWKSource<C> jwkSource) {
        if (jwkSource == null) {
            throw new IllegalArgumentException("The JWK source must not be null");
        }
        this.jwkSource = jwkSource;
    }

    @Override
    public List<? extends Key> selectJWSKeys(JWSHeader jwsHeader, C context) throws KeySourceException {
        if (!jwsAlgorithm.equals(jwsHeader.getAlgorithm())) {
            return Collections.emptyList();
        }
        JWKMatcher jwkMatcher = JWKMatcher.forJWSHeader(jwsHeader);
        if (jwkMatcher == null) {
            return Collections.emptyList();
        }
        List<JWK> jwkMatches = jwkSource.get(new JWKSelector(jwkMatcher), context);
        List<Key> sanitizedKeyList = new LinkedList<>();
        for (JWK jwk : jwkMatches) {
            if (jwk instanceof AsymmetricJWK) {
            }
        }
        return sanitizedKeyList;
    }
}
