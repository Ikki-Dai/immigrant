package com.ikki.immigrant.infrastructure.util;

import io.undertow.util.HexConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HexConvertTest {

    @Test
    void toStr() {
        byte[] bytes = new byte[]{15, 17};
        String str = HexConverter.convertToHexString(bytes);
        System.out.println(str);
        Assertions.assertNotNull(str);
    }

    @Test
    void toByte() {
        byte[] bytes = HexConverter.convertFromHex("0f11");
        for (byte b : bytes) {
            System.out.println(b);
        }
        Assertions.assertEquals(2, bytes.length);
    }


}
