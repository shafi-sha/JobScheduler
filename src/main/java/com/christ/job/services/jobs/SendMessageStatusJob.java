package com.christ.job.services.jobs;

import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
import com.christ.job.services.tasks.SendMessageStatusTask;
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
public class SendMessageStatusJob {

    @Autowired
    private SendMessageStatusTask sendMessageStatusTask;

    @Bean
    public Job sendMessageStatus(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
        return new JobBuilder("sendMessageStatus", jobRepository)
                .listener(listener)
                .flow(sendMessageStatusStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step sendMessageStatusStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendMessageStatusStep", jobRepository)
                .tasklet(sendMessageStatusTask, transactionManager)
                .build();
    }
}
