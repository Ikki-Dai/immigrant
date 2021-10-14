package com.ikki.immigrant.infrastructure.util;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface SensitiveMask {

    String mask() default "*****";

    int prefixLength() default 1;

    /**
     * start from last one
     *
     * @return
     */
    int suffixLength() default 1;

    /**
     * mask content length to keep
     * while length less than @link maskKeep,
     * will mask all content to ensure have content to be masked
     *
     * @return
     */
    int maskKeep() default 3;

    String locationBefore() default "@";
}
