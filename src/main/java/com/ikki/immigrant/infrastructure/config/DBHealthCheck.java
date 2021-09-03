package com.ikki.immigrant.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class DBHealthCheck {

    @Bean
    public HealthContributor fixDBHealthIndicator(@Qualifier("dbHealthContributor") HealthContributor healthContributor) {
        if (healthContributor instanceof DataSourceHealthIndicator) {
            DataSourceHealthIndicator dataSourceHealthIndicator = (DataSourceHealthIndicator) healthContributor;
            if (!StringUtils.hasText(dataSourceHealthIndicator.getQuery())) {
                dataSourceHealthIndicator.setQuery("select @@version");
            }
        }
        return healthContributor;
    }
}
