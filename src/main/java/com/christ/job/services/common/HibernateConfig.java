package com.christ.job.services.common;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.provider.impl.ReactiveEntityManagerFactoryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.redisson.spring.transaction.ReactiveRedissonTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.AbstractReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.sql.Driver;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean("reactive_hibernate")
    @Primary
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory("ReactivePU");
    }

    @Bean
    @Primary
    public Mutiny.SessionFactory reactiveSessionFactory(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(Mutiny.SessionFactory.class);
    }

    @Bean
    @Primary
    public Stage.SessionFactory futureSessionFactory(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(Stage.SessionFactory.class);
    }

//    @Bean
//    public DriverManagerDataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setUsername("Dev_Erp");
//        dataSource.setPassword("Tut06!l$2019");
//        dataSource.setUrl("jdbc:mysql://10.5.13.52:3306/ERP");
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        return dataSource;
//    }

//    @Bean
//    public PlatformTransactionManager hibernateTransactionManager(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
//        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//        transactionManager.setSessionFactory((SessionFactory) reactiveSessionFactory(entityManagerFactory));
//        transactionManager.setDataSource(dataSource());
//        return transactionManager;
//    }


//    @Bean("reactive_hibernate_batch")
//    @BatchDataSource
//    @Primary
//    public EntityManagerFactory entityManagerFactory1() {
//        return Persistence.createEntityManagerFactory("BatchPU");
//    }

//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        return Persistence.createEntityManagerFactory("ReactivePU");
//    }
//
//    @Bean
//    public Mutiny.SessionFactory reactiveSessionFactory(EntityManagerFactory entityManagerFactory) {
//        return entityManagerFactory.unwrap(Mutiny.SessionFactory.class);
//    }
//
//    @Bean
//    public Stage.SessionFactory futureSessionFactory(EntityManagerFactory entityManagerFactory) {
//        return entityManagerFactory.unwrap(Stage.SessionFactory.class);
//    }

    private static class Wrapper {
        EntityManagerFactory entityManagerFactory;
    }
}
