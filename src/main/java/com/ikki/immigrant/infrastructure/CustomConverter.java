package com.ikki.immigrant.infrastructure;

import io.undertow.util.HexConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcValue;

import java.sql.JDBCType;
import java.util.BitSet;
import java.util.EnumSet;

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
    public static class Enum2IntegerConverter<E extends Enum<E>> implements Converter<Enum<E>, JdbcValue> {

        @Override
        public JdbcValue convert(Enum anEnum) {
            return JdbcValue.of(anEnum.ordinal(), JDBCType.INTEGER);
        }
    }

    @WritingConverter
    public static class Es2StrConverter<E extends Enum<E>> implements Converter<EnumSet<E>, JdbcValue> {

        @Override
        public JdbcValue convert(EnumSet enumSet) {
            if (enumSet.isEmpty()) {
                return JdbcValue.of("", JDBCType.VARCHAR);
            }
            StringBuilder stringBuilder = new StringBuilder();
            int i;
            for (Object o : enumSet) {
                if (o instanceof Enum) {
                    i = ((Enum<?>) o).ordinal();
                    stringBuilder.append(i).append(",");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return JdbcValue.of(stringBuilder.toString(), JDBCType.VARCHAR);
        }

    }

    @ReadingConverter
    public static class Number2EnumConverter<E extends Enum<E>> implements Converter<Integer, E> {

        private final Class<E> type;
        private final E[] enums;

        public Number2EnumConverter(Class<E> type) {
            if (type == null) {
                throw new IllegalArgumentException("Type argument cannot be null");
            }
            this.type = type;
            this.enums = type.getEnumConstants();
            if (enums.length == 0) {
                throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
            }
        }

        @Override
        public E convert(Integer number) {
            try {
                return enums[number];
            } catch (Exception ex) {
                log.warn("Cannot convert {} to {} by ordinal value.", number, type.getSimpleName());
            }
            return null;
        }
    }

    @ReadingConverter
    public static class Str2EsConverter<E extends Enum<E>> implements Converter<String, EnumSet<E>> {
        private final Class<E> type;
        private final E[] enums;

        public Str2EsConverter(Class<E> type) {
            if (type == null) {
                throw new IllegalArgumentException("Type argument cannot be null");
            }
            this.type = type;
            this.enums = type.getEnumConstants();
            if (enums.length == 0) {
                throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
            }
        }

        @Override
        public EnumSet<E> convert(String s) {
            String[] sa = s.split(",");
            EnumSet<E> enumSet = EnumSet.noneOf(type);
            for (String e : sa) {
                enumSet.add(enums[Integer.parseInt(e)]);
            }
            return enumSet;
        }
    }
}
