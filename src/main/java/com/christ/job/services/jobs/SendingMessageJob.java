package com.christ.job.services.jobs;

import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
import com.christ.job.services.tasks.SendingMessageTask;
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
public class SendingMessageJob {

    @Autowired
    private SendingMessageTask sendingMessageTask;

    @Bean
    public Job sendingMessage(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
        return new JobBuilder("sendingMessage", jobRepository)
                .listener(listener)
                .flow(sendingMessageStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step sendingMessageStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendingMessageStep", jobRepository)
                .tasklet(sendingMessageTask, transactionManager)
                .build();
    }
}
