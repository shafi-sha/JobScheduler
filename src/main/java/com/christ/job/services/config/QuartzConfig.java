package com.christ.job.services.config;

import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import com.christ.job.services.common.Utils;
import jakarta.annotation.PostConstruct;
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
    private RedisSysPropertiesData redisSysPropertiesData;

    @Autowired
    private JobRegistry jobRegistry;

    @Bean
    public static JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry, PlatformTransactionManager transactionManager) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /*---------------JobDetails start---------------*/

    @Bean
    public JobDetail jobThreeDetail() {
        //set job parameters
        Map<String, Object> jobParameters = new HashMap<>();
        jobParameters.put("parameter1", "value1");
        jobParameters.put("parameter2", 10.5);
        //set job data
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
    public JobDetail sendMessageJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "sendMessage");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "SendMessageJob");
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("sendMessage")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail sendMessageStatusJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "sendMessageStatus");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "SendMessageStatusJob");
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("sendMessageStatus")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail refreshMailTokenJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "refreshMailToken");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "RefreshMailTokenJob");
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("refreshMailToken")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail sendMailJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "sendMail");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        jobDataMap.put("jobClass", "SendMailJob");
        return JobBuilder.newJob(QuartzJobLauncher.class)
                .withIdentity("sendMail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    /*---------------JobDetails end---------------*/

    /*---------------Triggers start---------------*/

    @Bean
    public Trigger jobThreeTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(jobThreeDetail())
                .withIdentity("jobThreeTrigger")
                //.withSchedule(CronScheduleBuilder.cronSchedule("* 0/2 * * * ?")) //trigger that fire at every 2 minutes
                .withSchedule(CronScheduleBuilder.cronSchedule("00 35 22 * * ?"))
                .build();
    }

    @Bean
    public Trigger sendMessageJobTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(sendMessageJobDetail())
                .withIdentity("sendMessageJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("15 10 20 * * ?"))
                .build();
    }

    @Bean
    public Trigger sendMessageStatusJobTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(sendMessageStatusJobDetail())
                .withIdentity("sendMessageStatusJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("45 10 21 * * ?"))
                .build();
    }

    @Bean
    public Trigger refreshMailTokenJobTrigger() {
        System.out.println("triggering refreshMailTokenJobTrigger");
        return TriggerBuilder
                .newTrigger()
                .forJob(refreshMailTokenJobDetail())
                .withIdentity("refreshMailTokenJobTrigger")
                //.withSchedule(CronScheduleBuilder.cronSchedule("30 31 21 * * ?"))//0 0/55 * * * ?  //0 32 * ? * *  //00 02 * ? * *
                .withSchedule(CronScheduleBuilder.cronSchedule("00 13 * ? * *"))//0 0/55 * * * ?  //0 32 * ? * *  //00 02 * ? * *
                .build();
    }

    @Bean
    public Trigger sendMailJobTrigger() {
        return TriggerBuilder
                .newTrigger()
                .forJob(sendMailJobDetail())
                .withIdentity("sendMailJobTrigger")
                //.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(15, 48))
                .withSchedule(CronScheduleBuilder.cronSchedule("00 22 * ? * *"))//30 44 * ? * * //00 47 22 * * ?  //0 0/2 * * * ?
                .build();
    }

    /*---------------Triggers end---------------*/

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        List<JobDetail> jobDetailsList = new ArrayList<>();
        List<Trigger> triggersList = new ArrayList<>();
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setQuartzProperties(quartzProperties());

//        if("true".equalsIgnoreCase(redisSysPropertiesData.getSysProperties(SysProperties.SEND_MAIL.name(), null, null))){
//            jobDetailsList.add(jobThreeDetail());
//            triggersList.add(jobThreeTrigger());
//        }
//
//        if("true".equalsIgnoreCase(redisSysPropertiesData.getSysProperties(SysProperties.SEND_MAIL.name(), null, null))){
//            jobDetailsList.add(sendMailJobDetail());
//            triggersList.add(sendMailJobTrigger());
//        }
        jobDetailsList.add(refreshMailTokenJobDetail());
        triggersList.add(refreshMailTokenJobTrigger());

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
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        final Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.scheduler.instanceName", "JobScheduler");
        quartzProperties.setProperty("org.quartz.threadPool.threadCount", "5");
        return quartzProperties;
    }

//    @Bean
//    public Properties quartzProperties1() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }

//    #scheduler name will be "JobScheduler"
//    #org.quartz.scheduler.instanceName = JobScheduler
//
//    #maximum of 5 jobs can be run simultaneously
//    #org.quartz.threadPool.threadCount = 5
//    #org.quartz.threadPool.threadPriority= Thread.NORM_PRIORITY(5)
//
//    #============================================================================
//    # Configure ThreadPool
//    #============================================================================
//
//    #org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
//    #org.quartz.threadPool.threadCount: 20
//    ##Thread.MAX_PRIORITY 10
//    #org.quartz.threadPool.threadPriority: 5


}
