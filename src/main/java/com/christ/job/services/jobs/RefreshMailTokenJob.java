package com.christ.job.services.jobs;

import com.christ.job.services.tasks.RefreshMailTokenTask;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RefreshMailTokenJob {

    @Autowired
    private RefreshMailTokenTask refreshMailTokenTask;

    @Bean
    public Job refreshMailToken(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("refreshMailToken", jobRepository)
                .flow(refreshMailTokenStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step refreshMailTokenStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("refreshMailTokenStep", jobRepository)
                .tasklet(refreshMailTokenTask, transactionManager)
                .build();
    }
}
