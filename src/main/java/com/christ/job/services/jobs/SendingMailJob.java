package com.christ.job.services.jobs;

import com.christ.job.services.tasks.SendingMailTask;
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
public class SendingMailJob {

    @Autowired
    private SendingMailTask sendingMailTask;

    @Bean
    public Job sendingMail(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("sendingMail", jobRepository)
                .flow(sendingMailStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step sendingMailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendingMailStep", jobRepository)
                .tasklet(sendingMailTask, transactionManager)
                .build();
    }
}
