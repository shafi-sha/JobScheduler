package com.christ.job.services.common;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.stage.Stage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateReactiveConfiguration {

    @Bean("reactive_hibernate")
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory("ReactivePU");
    }

    @Bean
    public Mutiny.SessionFactory reactiveSessionFactory(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(Mutiny.SessionFactory.class);
    }

    @Bean
    public Stage.SessionFactory futureSessionFactory(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(Stage.SessionFactory.class);
    }

    private static class Wrapper {
        EntityManagerFactory entityManagerFactory;
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

}
