package com.christ.job.services.jobs;

import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
import com.christ.job.services.tasks.TaskOne;
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
public class demoJobOne {

    @Autowired
    private TaskOne taskOne;

    @Bean
    public Job jobOne(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
        return new JobBuilder("jobOne", jobRepository)
                .listener(listener)
                .flow(jobOneStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step jobOneStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobOneStep", jobRepository)
                .tasklet(taskOne, transactionManager)
                .build();
    }
}
