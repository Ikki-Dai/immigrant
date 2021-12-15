package com.ikki.immigrant.infrastructure.filter;

import com.ikki.immigrant.domain.Tenant;
import ua_parser.Client;

/**
 * @author ikki
 */
public class CurrentUtil {

    private final static InheritableThreadLocal<Client> currentUserAgent = new InheritableThreadLocal<>();

    private final static InheritableThreadLocal<Tenant> currentTenant = new InheritableThreadLocal<>();


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
