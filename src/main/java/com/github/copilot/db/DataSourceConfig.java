package com.github.copilot.db;

import com.github.copilot.user.context.BaseContextHandler;
import com.github.copilot.user.context.UserContextConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "commonEntityManagerFactory",
        transactionManagerRef = "commonTransactionManager")
public class DataSourceConfig {

    @Autowired
    private PasswordDecryptor passwordDecryptor;
    @Autowired
    private DataBaseProperties dataBaseProperties;

    @Bean("commonDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(dataBaseProperties.getUrl())
                .username(dataBaseProperties.getUsername())
                .password(passwordDecryptor.decrypt(dataBaseProperties.getPassword()))
                .driverClassName(dataBaseProperties.getDiverClassName())
                .build();
    }

    @Bean(name = "commonEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sourceEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("commonDataSource") DataSource dataSource,
            JpaProperties jpaProperties,
            HibernateProperties hibernateProperties) {

        Map<String, Object> properties =
                hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());


        return builder
                .dataSource(dataSource)
                .properties(properties)
                .persistenceUnit("default") // Persistence unit name
                .build();
    }

    @Bean(name = "commonTransactionManager")
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // Implement the AuditorAware interface to return the current auditor
        // This is a placeholder implementation, replace with your actual user identification logic
        return () -> {
            String userId = BaseContextHandler.get(UserContextConstants.userIdHeader);
            return Optional.ofNullable(userId);
        };
    }
}