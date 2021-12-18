package com.ikki.immigrant.infrastructure.filter;

import com.ikki.immigrant.domain.Tenant;
import com.nimbusds.jwt.JWTClaimsSet;
import ua_parser.Client;

/**
 * @author ikki
 */
public class CurrentUtil {

    private static final InheritableThreadLocal<Client> currentUserAgent = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<Tenant> currentTenant = new InheritableThreadLocal<>();

    private static final InheritableThreadLocal<JWTClaimsSet> currentJwt = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<String> currentIpAddress = new InheritableThreadLocal<>();


    private CurrentUtil() {
    }

    /**
     * process userAgent
     *
     * @param client userAgent
     */
    protected static void addUserAgent(Client client) {
        currentUserAgent.set(client);
    }

    public static Client getUserAgent() {
        return currentUserAgent.get();
    }

    protected static void removeUserAgent() {
        currentUserAgent.remove();
    }

    /**
     * Tenant
     */

    static void addTenant(String authingStr) {
        // todo check and set correct Tenant;
        currentTenant.set(new Tenant());
    }

    static Tenant getTenant() {
        return currentTenant.get();
    }

    protected static void removeTenant() {
        currentTenant.remove();
    }

    /**
     * @param claimsSet
     */
    protected static void addJwtClaimsSet(JWTClaimsSet claimsSet) {
        currentJwt.set(claimsSet);
    }

    static JWTClaimsSet getClaimsSet() {
        return currentJwt.get();
    }

    protected static void removeClaimsSet() {
        currentJwt.remove();
    }

    /**
     * @param ipAddress
     */
    protected static void addIpAddress(String ipAddress) {
        currentIpAddress.set(ipAddress);
    }

    static String getIpAddress() {
        return currentIpAddress.get();
    }

    protected static void removeIpAddress() {
        currentIpAddress.remove();
    }
}
