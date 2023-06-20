package com.christ.job.services.common;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean(name="entityManagerFactory")
    @Primary
    public LocalSessionFactoryBean sessionFactory() {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        String [] packages = {"com.christ.job.services.dbobjects"};
        sessionFactory.setPackagesToScan(packages);
        return sessionFactory;
    }

    @Bean
    @Primary
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername("Dev_Erp");
        dataSource.setPassword("Tut06!l$2019");
        dataSource.setUrl("jdbc:mysql://10.5.13.52:3306/ERP");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }

    @Bean(name="secondaryDS")
    @BatchDataSource
    public DriverManagerDataSource batchDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername("Dev_Erp");
        dataSource.setPassword("Tut06!l$2019");
        dataSource.setUrl("jdbc:mysql://10.5.13.52:3306/JOB_SCHEDULER");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }

    @Bean
    @Primary
    public PlatformTransactionManager hibernateTransactionManager() {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("spring.jpa.hibernate.ddl-auto", "validate");
        hibernateProperties.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        return hibernateProperties;
    }
}
