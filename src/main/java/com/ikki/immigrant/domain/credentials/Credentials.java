package com.ikki.immigrant.domain.credentials;

public class Credentials {

    private long id;
    private long uid;
    /**
     * the credential content
     * such as public key, mac secret, password
     */
    private String blob;
    /**
     * a string to recognize device
     * such as
     */
    private String identifier;
    /**
     * and
     */
    private String alias;
    private Type type;

    public enum Type {
        UNKNOWN, PASSWORD, FIDO, TOTP
    }
}
