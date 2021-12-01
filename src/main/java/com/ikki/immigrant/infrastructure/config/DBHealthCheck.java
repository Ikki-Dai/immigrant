package com.ikki.immigrant.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.EncryptDataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShadowDataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DBHealthCheck {

    private final Collection<DataSourcePoolMetadataProvider> metadataProviders;
    private final DataSourcePoolMetadataProvider poolMetadataProvider;

    public DBHealthCheck(ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
        this.metadataProviders = metadataProviders.orderedStream().collect(Collectors.toList());
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(this.metadataProviders);
    }

    /**
     * because default actuator DbHealthContributor does not support sharding datasource shown as below
     * <p>
     * but HealthContributor check only one datasource each time
     *
     * @return
     */
    @Bean(name = "dbHealthContributor")
    @ConditionalOnClass({ShardingDataSource.class, MasterSlaveDataSource.class, EncryptDataSource.class, ShadowDataSource.class})
//    @ConditionalOnBean(name = {"shardingDataSource", "masterSlaveDataSource", "encryptDataSource", "shadowDataSource"})
    @ConditionalOnProperty(
            prefix = "spring.shardingsphere",
            name = {"enabled"},
            havingValue = "true",
            matchIfMissing = true
    )
    public HealthContributor fixDBHealthIndicator(Map<String, DataSource> dataSources, DataSourceHealthIndicatorProperties dataSourceHealthIndicatorProperties) {
        if (dataSourceHealthIndicatorProperties.isIgnoreRoutingDataSources()) {
            Map<String, DataSource> filteredDataSources = dataSources.entrySet().stream().filter(e -> !(e.getValue() instanceof AbstractRoutingDataSource)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return this.createContributor(filteredDataSources);
        } else {
            return this.createContributor(dataSources);
        }
    }

    private HealthContributor createContributor(Map<String, DataSource> beans) {
        Assert.notEmpty(beans, "Beans must not be empty");
        if (beans.size() == 1) {
            DataSource dataSource = beans.values().iterator().next();
            if (dataSource instanceof ShardingDataSource) {
                return CompositeHealthContributor.fromMap(((ShardingDataSource) dataSource).getDataSourceMap(), this::createContributor);
            }
            return this.createContributor(beans.values().iterator().next());

        } else {
            return CompositeHealthContributor.fromMap(beans, this::createContributor);
        }
    }

    private String getValidationQuery(DataSource source) {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(source);
        return poolMetadata != null ? poolMetadata.getValidationQuery() : null;
    }

    private HealthContributor createContributor(DataSource source) {
        if (source instanceof AbstractRoutingDataSource) {
            AbstractRoutingDataSource routingDataSource = (AbstractRoutingDataSource) source;
            return new RoutingDataSourceHealthContributor(routingDataSource, this::createContributor);
        } else {
            return new DataSourceHealthIndicator(source, this.getValidationQuery(source));
        }
    }


    static class RoutingDataSourceHealthContributor implements CompositeHealthContributor {
        private final CompositeHealthContributor delegate;

        RoutingDataSourceHealthContributor(AbstractRoutingDataSource routingDataSource, Function<DataSource, HealthContributor> contributorFunction) {
            Map<String, DataSource> routedDataSources = routingDataSource.getResolvedDataSources().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
            this.delegate = CompositeHealthContributor.fromMap(routedDataSources, contributorFunction);
        }

        public HealthContributor getContributor(String name) {
            return this.delegate.getContributor(name);
        }

        public Iterator<NamedContributor<HealthContributor>> iterator() {
            return this.delegate.iterator();
        }
    }
}
