package com.ikki.immigrant.infrastructure.filter;

import com.ikki.immigrant.domain.Tenant;
import ua_parser.Client;

/**
 * @author ikki
 */
public class CurrentUtil {

    private static final InheritableThreadLocal<Client> currentUserAgent = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<Tenant> currentTenant = new InheritableThreadLocal<>();

    private CurrentUtil() {
    }

    /**
     * process userAgent
     *
     * @param client userAgent
     */
    static void addUserAgent(Client client) {
        currentUserAgent.set(client);
    }

    public static Client getUserAgent() {
        return currentUserAgent.get();
    }

    static void removeUserAgent() {
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

    static void removeTenant() {
        currentTenant.remove();
    }
}
