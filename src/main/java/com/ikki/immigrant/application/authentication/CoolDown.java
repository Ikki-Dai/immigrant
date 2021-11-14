package com.ikki.immigrant.application.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author ikki
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoolDown {

    int maxAttempts() default 6;

    int clearAfter() default 24;

    long[] interval() default {0, 0, 60, 120, 300};

    TimeUnit timeUnit() default TimeUnit.HOURS;

    String value();

}
