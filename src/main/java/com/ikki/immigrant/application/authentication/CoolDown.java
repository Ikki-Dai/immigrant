package com.ikki.immigrant.application.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ikki
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoolDown {

    int maxAttempts() default 6;

    int clearAfter() default 86400;

    /**
     * seconds
     *
     * @return
     */
    long[] interval() default {0, 0, 60, 120, 300};

    String value();

}
