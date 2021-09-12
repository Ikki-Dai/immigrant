package com.ikki.immigrant.domain.credentials;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Getter
@Setter
@ToString
public class Credentials {

    @Id
    private long id;

    private long uid;
    /**
     * the credential content
     * such as public key, mac secret, password
     */
    private String credential;
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
