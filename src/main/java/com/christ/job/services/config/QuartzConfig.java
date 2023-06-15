package com.christ.job.services.config;

import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import org.quartz.*;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.*;

@Configuration
public class QuartzConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobLocator jobLocator;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry, PlatformTransactionManager transactionManager) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /*---------------JobDetails start---------------*/

    @Bean
    public JobDetail jobOneDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "jobOne");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "demoJobOne");
        return JobBuilder.newJob(QuartzJobLauncher.class)
            .withIdentity("jobOne")
            .setJobData(jobDataMap)
            .storeDurably()
            .build();
    }

    @Bean
    public JobDetail jobTwoDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "jobTwo");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "demoJobTwo");
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("jobTwo")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail jobThreeDetail() {
        //set job parameters
        Map<String, Object> jobParameters = new HashMap<>();
        jobParameters.put("parameter1", "value1");
        jobParameters.put("parameter2", 10.5);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "jobThree");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "demoJobThree");
        jobDataMap.put("jobParametersMap", jobParameters);
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("jobThree")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail smsJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "sendingMessage");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "SendingMessageJob");
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("sendingMessage")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

//
//    @Bean
//    public JobDetail jobFourDetail() {
//        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("jobName", "demoJobFour");
//        jobDataMap.put("jobLauncher", jobLauncher);
//        jobDataMap.put("jobLocator", jobLocator);
//        return JobBuilder.newJob(QuartzJobLauncher.class)
//                .withIdentity("demoJobFour")
//                .setJobData(jobDataMap)
//                .storeDurably()
//                .build();
//    }

    /*---------------JobDetails end---------------*/

    /*---------------Triggers start---------------*/

    @Bean
    public Trigger jobOneTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(jobOneDetail())
                .withIdentity("jobOneTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("15 07 18 * * ?"))
                //.withSchedule(CronScheduleBuilder.cronSchedule("30 47 12 ? * *")) //trigger that fire at 10:15 AM every day
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0/3 15,18 * * ?")) //trigger that fire every 1 minutes starting at 12:00 PM and ending at 2:55 PM, AND fire every 5 minutes starting at 6:00 PM and ending at 6:55 PM, every day
                //.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .build();
    }

    @Bean
    public Trigger jobTwoTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(jobTwoDetail())
                .withIdentity("jobTwoTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("30 35 18 * * ?"))
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 13,18 * * ?")) //trigger that fire every 2 minutes starting at 1:00 PM and ending at 2:55 PM, AND fire every 5 minutes starting at 6:00 PM and ending at 6:55 PM, every day
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 15 10 ? * MON-FRI")) //trigger that fire at 10:15 AM every Monday, Tuesday, Wednesday, Thursday and Friday
                .build();
    }

    @Bean
    public Trigger jobThreeTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(jobThreeDetail())
                .withIdentity("jobThreeTrigger")
                //.withSchedule(CronScheduleBuilder.cronSchedule("* 0/2 * * * ?")) //trigger that fire at every 2 minutes
                .withSchedule(CronScheduleBuilder.cronSchedule("00 35 18 * * ?"))
                .build();
    }

    @Bean
    public Trigger smsJobTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(smsJobDetail())
                .withIdentity("smsJobTrigger")
                //.withSchedule(CronScheduleBuilder.cronSchedule("* 0/2 * * * ?")) //trigger that fire at every 2 minutes
                .withSchedule(CronScheduleBuilder.cronSchedule("45 31 17 * * ?"))
                .build();
    }

//
//    @Bean
//    public Trigger jobFourTrigger() {
//        return TriggerBuilder
//                .newTrigger()
//                .forJob(jobFourDetail())
//                .withIdentity("jobFourTrigger")
//                //.withSchedule(CronScheduleBuilder.cronSchedule("* 0/2 * * * ?")) //trigger that fire at every 2 minutes
//                .withSchedule(CronScheduleBuilder.cronSchedule("40 53 16 * * ?"))
//                .build();
//    }

    /*---------------Triggers end---------------*/

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        List<JobDetail> jobDetailsList = new ArrayList<>();
        List<Trigger> triggersList = new ArrayList<>();
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setQuartzProperties(quartzProperties());

        //if("true".equalsIgnoreCase(redisSysPropertiesData.getSysProperties(SysProperties.SEND_MAIL.name(), null, null))){
            jobDetailsList.add(jobOneDetail());
            triggersList.add(jobOneTrigger());
        //}

        //if("true".equalsIgnoreCase(redisSysPropertiesData.getSysProperties(SysProperties.SEND_MAIL.name(), null, null))){
            jobDetailsList.add(jobTwoDetail());
            triggersList.add(jobTwoTrigger());
        //}

//        if("true".equalsIgnoreCase(redisSysPropertiesData.getSysProperties(SysProperties.SEND_MAIL.name(), null, null))){
            jobDetailsList.add(jobThreeDetail());
            triggersList.add(jobThreeTrigger());
//        }
//
//        if("true".equalsIgnoreCase(redisSysPropertiesData.getSysProperties(SysProperties.SEND_MAIL.name(), null, null))){
//            jobDetailsList.add(jobFourDetail());
//            triggersList.add(jobFourTrigger());
//        }

        jobDetailsList.add(smsJobDetail());
        triggersList.add(smsJobTrigger());

        /*
        * setting job details for all the jobs
        * setting triggers for all the jobs
        */
        scheduler.setJobDetails(
                jobDetailsList.toArray(JobDetail[]::new)
        );
        scheduler.setTriggers(
                triggersList.toArray(Trigger[]::new)
        );
//        scheduler.setJobDetails(
//                jobOneDetail(), jobTwoDetail(), jobThreeDetail(), jobFourDetail()
//        );
//        scheduler.setTriggers(
//                jobOneTrigger(), jobTwoTrigger(), jobThreeTrigger(), jobFourTrigger()
//        );
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
