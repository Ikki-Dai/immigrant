package com.ikki.immigrant.infrastructure.convert;

import io.undertow.util.HexConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcValue;

import java.sql.JDBCType;
import java.util.BitSet;

@Slf4j
public class CustomConverter {

    @WritingConverter
    public enum BitSet2StrConvert implements Converter<BitSet, String> {
        INSTANCE;

        @Override
        public String convert(BitSet source) {
            byte[] bytes = source.toByteArray();
            return HexConverter.convertToHexString(bytes);
        }
    }

    @ReadingConverter
    public enum Str2BitSetConvert implements Converter<String, BitSet> {
        INSTANCE;

        @Override
        public BitSet convert(String source) {
            byte[] bytes = HexConverter.convertFromHex(source);
            return BitSet.valueOf(bytes);
        }
    }

    @WritingConverter
    public enum Enum2Integer implements Converter<Enum, JdbcValue> {
        INSTANCE;

        @Override
        public JdbcValue convert(Enum anEnum) {
            return JdbcValue.of(anEnum.ordinal(), JDBCType.INTEGER);
        }
    }

    @ReadingConverter
    public static class Integer2Enum<E extends Enum> implements Converter<Integer, E> {

        private final Class<E> type;
        private final E[] enums;

        public Integer2Enum(Class<E> type) {
            if (type == null) {
                throw new IllegalArgumentException("Type argument cannot be null");
            }
            this.type = type;
            this.enums = type.getEnumConstants();
            if (this.enums == null) {
                throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
            }
        }

        @Override
        public E convert(Integer ordinal) {
//            try {
//                return enums[ordinal];
//            } catch (Exception ex) {
//                log.warn("Cannot convert {} to {} by ordinal value.", ordinal, type.getSimpleName());
//            }
            return null;
        }
    }
}
