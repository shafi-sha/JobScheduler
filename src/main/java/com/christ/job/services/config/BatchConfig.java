package com.christ.job.services.config;

//import com.christ.job.services.common.HibernateConfig;
//import com.christ.job.services.dbobjects.common.ErpCampusDBO;
//import com.christ.job.services.jobNotificationListeners.JobCompletionNotificationListener;
////import com.christ.job.services.processors.TaskFourProcessor;
//import com.christ.job.services.jobs.demoJobOne;
//import com.christ.job.services.jobs.demoJobThree;
//import com.christ.job.services.jobs.demoJobTwo;
//import com.christ.job.services.processors.TaskOneProcessor;
////import com.christ.job.services.readers.TaskFourReader;
//import com.christ.job.services.readers.TaskOneReader;
//import com.christ.job.services.tasks.TaskOne;
//import com.christ.job.services.tasks.TaskTwo;
////import com.christ.job.services.writers.TaskFourWriter;
//import com.christ.job.services.writers.TaskOneWriter;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.batch.BatchDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.ReactiveTransactionManager;
//
//import java.util.List;
////
////@Configuration
//////@EnableBatchProcessing
////public class BatchConfig {
////
//////    @Autowired
//////    private TaskOne taskOne;
//////
//////    @Autowired
//////    private TaskTwo taskTwo;
////
////    @Autowired
////    private TaskOneReader taskOneReader;
////
////    @Autowired
////    private TaskFourReader taskFourReader;
////
////    @Autowired
////    private TaskFourProcessor taskFourProcessor;
////
////    @Autowired
////    private TaskFourWriter taskFourWriter;
////
//////    @Bean
//////    public Job demoJobOne(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
//////        return new JobBuilder("demoJobOne", jobRepository)
//////                .listener(listener)
//////                .flow(step1(jobRepository, transactionManager))
//////                .end()
//////                .build();
//////    }
//////
//////    @Bean
//////    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//////        return new StepBuilder("step1", jobRepository)
//////            .tasklet(taskOne, transactionManager)
//////            .build();
//////    }
//////
//////    @Bean
//////    public Job demoJobTwo(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
//////        return new JobBuilder("demoJobTwo", jobRepository)
//////                .listener(listener)
//////                .flow(step2(jobRepository, transactionManager))
//////                .end()
//////                .build();
//////    }
//////
//////    @Bean
//////    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//////        return new StepBuilder("step2", jobRepository)
//////                .tasklet(taskTwo, transactionManager)
//////                .build();
//////    }
////
////    @Bean
////    public Job demoJobThree(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
////        return new JobBuilder("demoJobThree", jobRepository)
////                .listener(listener)
////                .flow(step3(jobRepository, transactionManager))
////                .end()
////                .build();
////    }
////
////    @Bean
////    public Step step3(JobRepository jobRepository,
////        PlatformTransactionManager transactionManager) {
////        return new StepBuilder("step3", jobRepository)
////            .<List<ErpCampusDBO>, List<ErpCampusDBO>> chunk(10, transactionManager)
////            .reader(taskOneReader)
////            .processor(new TaskOneProcessor())
////            .writer(new TaskOneWriter())
////            .build();
////    }
////
////    @Bean
////    public Job demoJobFour(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobCompletionNotificationListener listener) {
////        return new JobBuilder("demoJobFour", jobRepository)
////                .listener(listener)
////                .flow(step4(jobRepository, transactionManager))
////                .end()
////                .build();
////    }
////
////    @Bean
////    public Step step4(JobRepository jobRepository,
////                      PlatformTransactionManager transactionManager) {
////        return new StepBuilder("step4", jobRepository)
////                .<ErpCampusDBO, ErpCampusDBO> chunk(10, transactionManager)
////                .reader(taskFourReader)
////                .processor(taskFourProcessor)
////                .writer(taskFourWriter)
////                .build();
////    }
////}
//
//
////@Configuration
//////@Import({demoJobOne.class, demoJobTwo.class, demoJobThree.class})
////public class BatchConfig {
////
////}