package com.ikki.immigrant.infrastructure.config;

import com.ikki.immigrant.domain.subject.Subject;
import com.ikki.immigrant.infrastructure.convert.CustomConverter;
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
                CustomConverter.Enum2Integer.INSTANCE,
                new CustomConverter.Integer2Enum(Subject.UsableStatus.class),
                CustomConverter.Es2StrConverter.INSTANCE,
                new CustomConverter.Str2EsConverter<>(Subject.ValidStatus.class)
        ));
    }

}
