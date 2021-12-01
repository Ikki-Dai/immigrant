package com.ikki.immigrant.infrastructure.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SensitiveSerializer extends StdSerializer<String> implements ContextualSerializer {
    private String maskStr = "??";

    private transient SensitiveMask sensitiveMask;

    protected SensitiveSerializer() {
        super(String.class);
    }

    public SensitiveSerializer(SensitiveMask sensitiveMask) {
        super(String.class);
        this.sensitiveMask = sensitiveMask;
    }


    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (null == sensitiveMask) {
            throw new JsonGenerationException("not found valid sensitiveMask Annotation", jsonGenerator);
        }

        if (s.isEmpty()) {
            jsonGenerator.writeString(s);
            return;
        }

        int start = sensitiveMask.prefixLength();
        int end = sensitiveMask.suffixLength();

        maskStr = sensitiveMask.mask();
        int maskLength = sensitiveMask.maskKeep();
        String locStr = sensitiveMask.locationBefore();

        int index = s.lastIndexOf(locStr);

        end = index > 0 ? index - end : s.length() - end;
        // mask content too short
        if (start < 0 || start >= end || start > s.length() || (end - start) < maskLength || end > s.length()) {
            log.warn("mask all occur param: startIndex:[{}], endIndex:[{}] with context: [{}], loc:[{}]", start, end, s, locStr);
            start = 0;
            end = index > 0 ? index : s.length() - 1;
        }

        String ns = new StringBuilder(s).replace(start, end, maskStr).toString();
        jsonGenerator.writeString(ns);
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        log.debug("mask [{}]", property.getMember().getFullName());
        return new SensitiveSerializer(property.getAnnotation(SensitiveMask.class));
    }
}
