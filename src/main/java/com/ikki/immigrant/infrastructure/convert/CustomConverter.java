package com.ikki.immigrant.infrastructure.convert;

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
    public enum Enum2Integer implements Converter<Enum, JdbcValue> {
        INSTANCE;

        @Override
        public JdbcValue convert(Enum anEnum) {
            return JdbcValue.of(anEnum.ordinal(), JDBCType.INTEGER);
        }
    }

    @WritingConverter
    public enum Es2StrConverter implements Converter<EnumSet, JdbcValue> {
        INSTANCE;

        @Override
        public JdbcValue convert(EnumSet enumSet) {
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

    @ReadingConverter
    public static class Str2EsConverter<E extends Enum> implements Converter<String, EnumSet> {
        private final Class<E> type;
        private final E[] enums;

        public Str2EsConverter(Class<E> type) {
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
        public EnumSet convert(String s) {
            String[] sa = s.split(",");
            EnumSet enumSet = EnumSet.noneOf(type);
            for (String e : sa) {
                enumSet.add(enums[Integer.valueOf(e)]);
            }
            return enumSet;
        }
    }
}
