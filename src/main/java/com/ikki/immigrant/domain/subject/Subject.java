package com.ikki.immigrant.domain.subject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@Table
@ToString
public class Subject {
    private long id;
    @Id
    private long uid;
    private String phone;
    private String email;
    private Status status;
    @CreatedDate
    private Instant signupTime;
    @LastModifiedDate
    private Instant updateTime;

    public enum Status {
        UNKNOWN,
        UN_VERIFIED,
        EMAIL_VERIFIED,
        PHONE_VERIFIED,
        BOTH_VERIFIED,
        NORMAL,
        FREEZE,
        BANNED
    }

}
