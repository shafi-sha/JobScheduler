package com.christ.job.services.jobs;

import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
import com.christ.job.services.tasks.TaskTwo;
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
public class demoJobTwo {

    @Autowired
    private TaskTwo taskTwo;

    @Bean
    public Job jobTwo(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
        return new JobBuilder("jobTwo", jobRepository)
                .listener(listener)
                .flow(jobTwoStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step jobTwoStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobTwoStep", jobRepository)
                .tasklet(taskTwo, transactionManager)
                .build();
    }
}
