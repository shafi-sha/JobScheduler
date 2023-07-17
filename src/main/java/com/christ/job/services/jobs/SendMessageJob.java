package com.christ.job.services.jobs;

import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
import com.christ.job.services.tasks.SendMessageTask;
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
public class SendMessageJob {

    @Autowired
    private SendMessageTask sendMessageTask;

    @Bean
    public Job sendMessage(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
        return new JobBuilder("sendMessage", jobRepository)
                .listener(listener)
                .flow(sendMessageStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step sendMessageStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendMessageStep", jobRepository)
                .tasklet(sendMessageTask, transactionManager)
                .build();
    }
}
