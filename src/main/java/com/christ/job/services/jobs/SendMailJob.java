package com.christ.job.services.jobs;

import com.christ.job.services.tasks.SendMailTask;
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
public class SendMailJob {

    @Autowired
    private SendMailTask sendMailTask;

    @Bean
    public Job sendMail(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("sendMail", jobRepository)
                .flow(sendMailStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step sendMailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sendMailStep", jobRepository)
                .tasklet(sendMailTask, transactionManager)
                .build();
    }
}
