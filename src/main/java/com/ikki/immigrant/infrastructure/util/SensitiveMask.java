package com.ikki.immigrant.infrastructure.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveMask {

    int start() default 1;

    /**
     * start from last one
     *
     * @return
     */
    int end() default -1;

    String startIndexExpress() default "";

    String endIndexExpress() default "";
}
