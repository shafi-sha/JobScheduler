package com.christ.job.services.jobs;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
import com.christ.job.services.processors.TaskThreeProcessor;
import com.christ.job.services.readers.TaskThreeReader;
import com.christ.job.services.writers.TaskThreeWriter;
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
public class demoJobThree {

    @Autowired
    private TaskThreeReader taskThreeReader;

    @Autowired
    private TaskThreeProcessor taskThreeProcessor;

    @Autowired
    private TaskThreeWriter taskThreeWriter;

    @Bean
    public Job jobThree(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
        return new JobBuilder("jobThree", jobRepository)
                .listener(listener)
                .flow(jobThreeStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    public Step jobThreeStep(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobThreeStep", jobRepository)
                .<ErpCampusDBO, ErpCampusDBO> chunk(5, transactionManager)
                .reader(taskThreeReader)
                .processor(taskThreeProcessor)
                .writer(taskThreeWriter)
                .build();
    }

}
