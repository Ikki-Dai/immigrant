package com.ikki.immigrant.infrastructure.config;

import com.ikki.immigrant.domain.credentials.Credentials;
import com.ikki.immigrant.domain.subject.entity.Subject;
import com.ikki.immigrant.infrastructure.CustomConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

import java.util.Arrays;

@Configuration
@EnableJdbcAuditing
public class DataJdbcConfiguration extends AbstractJdbcConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new TokenContextAuditor();
    }

    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                CustomConverter.BitSet2StrConvert.INSTANCE,
                CustomConverter.Str2BitSetConvert.INSTANCE,
                new CustomConverter.Enum2IntegerConverter<>(),
                new CustomConverter.Number2EnumConverter<>(Subject.AvailableStatus.class),
                new CustomConverter.Es2StrConverter<>(),
                new CustomConverter.Str2EsConverter<>(Subject.ValidStatus.class),

                new CustomConverter.Number2EnumConverter<>(Credentials.Type.class)

        ));
    }

}
