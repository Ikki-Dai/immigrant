package com.ikki.immigrant.domain.subject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.EnumSet;

/**
 * subject have an obvious lifecycle marked as marked by {@link AvailableStatus}
 * <p>
 * UNKNOWN --> NORMAL --> FREEZE --> BANNED
 *
 * <p>
 *
 *
 * <p>
 * UNKNOWN: just after sign-up, but phone or email not valid status
 * <p>
 * NORMAL: can be used to sign-in and do any operation
 * <p>
 * FREEZE: a policy after some invalid operation. such as authenticate failed after 3 times.
 * at this time, should  reject authentication request directly
 * <p>
 * BANNED: forbidden any type request
 *
 * @author ikki
 */

@Getter
@Setter
@Table
@ToString
public class Subject {
    @Id
    private long uid;
    private String username;
    private String phone;
    private String email;
    private EnumSet<ValidStatus> valid;
    private AvailableStatus available;
    @CreatedDate
    private Instant signupTime;
    @LastModifiedDate
    private Instant updateTime;

    public enum ValidStatus {
        UN_VERIFIED,  // only password
        EMAIL_VERIFIED, // can login by email code
        PHONE_VERIFIED, // can login by phone code
        TOTP_VERIFIED, // totp verification enabled
        FIDO_VERIFIED
    }

    public enum AvailableStatus {
        UNKNOWN,
        NORMAL,
        FREEZE,
        BANNED
    }

}
