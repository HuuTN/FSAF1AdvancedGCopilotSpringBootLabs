package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;

/**
 * Database configuration class for the ecommerce platform.
 * Configures Oracle database connection, HikariCP connection pool,
 * and transaction management.
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    @Value("${spring.jpa.hibernate.ddl-auto:create}")
    private String ddlAuto;

    @Value("${spring.jpa.show-sql:true}")
    private boolean showSql;

    @Value("${spring.jpa.properties.hibernate.format_sql:true}")
    private boolean formatSql;

    /**
     * Primary DataSource configuration using HikariCP connection pool.
     * Optimized for Oracle database connections.
     */
    @Bean
    @Primary
    @Profile("!test")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Basic connection properties
        config.setJdbcUrl(datasourceUrl);
        config.setUsername(datasourceUsername);
        config.setPassword(datasourcePassword);
        config.setDriverClassName(driverClassName);

        // Connection pool settings
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);

        // Pool name for monitoring
        config.setPoolName("EcommerceHikariCP");

        // Connection validation
        config.setConnectionTestQuery("SELECT 1 FROM DUAL");
        config.setValidationTimeout(5000);

        // Performance optimizations for Oracle
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Oracle specific settings to fix commit issues
        config.setAutoCommit(false);
        config.addDataSourceProperty("oracle.jdbc.autoCommitSpecCompliant", "false");
        config.addDataSourceProperty("oracle.net.CONNECT_TIMEOUT", "30000");
        config.addDataSourceProperty("oracle.net.READ_TIMEOUT", "60000");

        return new HikariDataSource(config);
    }

    /**
     * EntityManagerFactory configuration with Hibernate as JPA provider.
     */
    @Bean("entityManagerFactory")
    @Primary
    @Profile("!test")
    public LocalContainerEntityManagerFactoryBean prodEntityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("com.example.model.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(showSql);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.OracleDialect");

        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setJpaProperties(hibernateProperties());

        return entityManagerFactory;
    }

    /**
     * Hibernate-specific properties configuration.
     */
    private Properties hibernateProperties() {
        Properties properties = new Properties();

        // Basic Hibernate properties
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        properties.setProperty("hibernate.show_sql", String.valueOf(showSql));
        properties.setProperty("hibernate.format_sql", String.valueOf(formatSql));

        // Performance and behavior settings
        properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
        properties.setProperty("hibernate.id.new_generator_mappings", "true");
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");

        // Batch processing
        properties.setProperty("hibernate.jdbc.batch_size", "25");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Connection handling
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.connection.release_mode", "after_transaction");

        return properties;
    }

    /**
     * Transaction manager configuration.
     * Enhanced for Oracle database transaction handling.
     */
    @Bean("transactionManager")
    @Primary
    @Profile("!test")
    public PlatformTransactionManager prodTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        // Oracle specific transaction settings
        transactionManager.setDefaultTimeout(60); // 60 seconds timeout
        transactionManager.setRollbackOnCommitFailure(true);
        transactionManager.setFailEarlyOnGlobalRollbackOnly(true);
        transactionManager.setNestedTransactionAllowed(true);

        return transactionManager;
    }

    /**
     * JdbcTemplate for native SQL operations.
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setQueryTimeout(30);
        return jdbcTemplate;
    }

    /**
     * Test-specific DataSource configuration for integration tests.
     * Uses H2 in-memory database for faster test execution.
     */
    @Bean
    @Primary
    @Profile("test")
    public DataSource testDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=Oracle");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(5);
        config.setPoolName("TestHikariCP");

        return new HikariDataSource(config);
    }

    /**
     * Test-specific EntityManagerFactory configuration with H2.
     */
    @Bean("entityManagerFactory")
    @Primary
    @Profile("test")
    public LocalContainerEntityManagerFactoryBean testEntityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("com.example.model.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");

        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setJpaProperties(testHibernateProperties());

        return entityManagerFactory;
    }

    /**
     * Test-specific Hibernate properties for H2.
     */
    private Properties testHibernateProperties() {
        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
        properties.setProperty("hibernate.id.new_generator_mappings", "true");
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");

        return properties;
    }

    /**
     * Test-specific transaction manager configuration.
     */
    @Bean("transactionManager")
    @Primary
    @Profile("test")
    public PlatformTransactionManager testTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        // Test specific transaction settings
        transactionManager.setDefaultTimeout(30); // 30 seconds timeout for tests
        transactionManager.setRollbackOnCommitFailure(true);
        transactionManager.setFailEarlyOnGlobalRollbackOnly(true);
        transactionManager.setNestedTransactionAllowed(true);

        return transactionManager;
    }

    /**
     * Development-specific DataSource configuration.
     * Includes additional debugging and monitoring features.
     */
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(datasourceUrl);
        config.setUsername(datasourceUsername);
        config.setPassword(datasourcePassword);
        config.setDriverClassName(driverClassName);

        // Development-specific settings
        config.setMaximumPoolSize(5); // Smaller pool for development
        config.setMinimumIdle(2);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName("DevHikariCP");

        // Enable metrics and monitoring
        config.setRegisterMbeans(true);
        config.setMetricRegistry(null); // Can be configured with Micrometer

        return new HikariDataSource(config);
    }
}
