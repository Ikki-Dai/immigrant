package com.ikki.immigrant.infrastructure.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SensitiveSerializer extends StdSerializer<String> implements ContextualSerializer {

    private static final String STR_MASK = "****";
    private static final SensitiveSerializer INSTANCE = new SensitiveSerializer();
    private transient SensitiveMask sensitiveMask;

    public SensitiveSerializer() {
        super(String.class);
    }

    public SensitiveSerializer(SensitiveMask sensitiveMask) {
        super(String.class);
        this.sensitiveMask = sensitiveMask;
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (s.isEmpty()) {
            jsonGenerator.writeString(s);
            return;
        }

        int start = 1;
        int end = -1;
        String ns;
        if (null != sensitiveMask) {
            start = sensitiveMask.start();
            end = sensitiveMask.end();
        }
        end += s.length();
        if (start > end || end < 0 || start > s.length()) {
            // if unexpected index occurs, all data mask
            jsonGenerator.writeString(STR_MASK);
            return;
        }
        ns = new StringBuilder(s).replace(start, end, STR_MASK).toString();
        jsonGenerator.writeString(ns);
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
//        SensitiveMask sensitiveMask = null;
        if (null != property) {
            sensitiveMask = property.getAnnotation(SensitiveMask.class);
        }
        if (null != sensitiveMask) {
            return new SensitiveSerializer(sensitiveMask);
        }
        return INSTANCE;
    }
}
