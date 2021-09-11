package com.ikki.immigrant.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.convert.JdbcValue;

import java.util.BitSet;
import java.util.EnumSet;

class ConverterTest {

    @Test
    void BitSet2StrTest() {
        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(3);
        String str = CustomConverter.BitSet2StrConvert.INSTANCE.convert(bitSet);
        Assertions.assertTrue("0a".equalsIgnoreCase(str));
    }

    @Test
    void Str2BitSetTest() {
        BitSet bitSet = CustomConverter.Str2BitSetConvert.INSTANCE.convert("0a");
        System.out.println(bitSet);
        Assertions.assertNotNull(bitSet);
    }

    @Test
    void enum2IntTest() {
        CustomConverter.Enum2IntegerConverter<TestEnum> converter = new CustomConverter.Enum2IntegerConverter<>();
        JdbcValue jdbcValue = converter.convert(TestEnum.V1);
        Assertions.assertEquals(1, jdbcValue.getValue());
    }

    @Test
    void Es2StrTest() {
        Converter<EnumSet<TestEnum>, JdbcValue> converter = new CustomConverter.Es2StrConverter<>();
        JdbcValue jdbcValue = converter.convert(EnumSet.of(TestEnum.V0, TestEnum.V2));
        Assertions.assertTrue("0,2".equalsIgnoreCase(String.valueOf(jdbcValue.getValue())));
    }

    @Test
    void Number2EnumTest() {
        CustomConverter.Number2EnumConverter<TestEnum> converter = new CustomConverter.Number2EnumConverter<>(TestEnum.class);
        TestEnum testEnum = converter.convert(2);
        Assertions.assertEquals(TestEnum.V2, testEnum);
    }

    @Test
    void String2EsTest() {
        CustomConverter.Str2EsConverter<TestEnum> converter = new CustomConverter.Str2EsConverter<>(TestEnum.class);
        EnumSet<TestEnum> enumSet = converter.convert("1,2");
        Assertions.assertTrue(enumSet.contains(TestEnum.V1));
    }

    @Test
    public void exceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CustomConverter.Str2EsConverter<>(null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CustomConverter.Str2EsConverter<>(fakeEnum.class);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CustomConverter.Number2EnumConverter<>(null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CustomConverter.Number2EnumConverter<>(fakeEnum.class);
        });
    }

    enum fakeEnum {

    }


    enum TestEnum {
        V0, V1, V2
    }
}
