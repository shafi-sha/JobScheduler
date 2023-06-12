package com.christ.job.services.config;

import com.christ.job.services.common.Utils;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class QuartzJobLauncher extends QuartzJobBean {

    private String jobName;
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;

    private HashMap<String, Object> jobParametersMap;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobLauncher getJobLauncher() {
        return jobLauncher;
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public JobLocator getJobLocator() {
        return jobLocator;
    }

    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    public HashMap<String, Object> getJobParametersMap() {
        return jobParametersMap;
    }

    public void setJobParametersMap(HashMap<String, Object> jobParametersMap) {
        this.jobParametersMap = jobParametersMap;
    }

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            Job job = jobLocator.getJob(jobName);
//            JobParameters params = new JobParametersBuilder()
//                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
//                    .toJobParameters();
//            jobLauncher.run(job, params);

            JobParametersBuilder builder = new JobParametersBuilder();
            if(!Utils.isNullOrEmpty(jobParametersMap)){
                for (Map.Entry<String, Object> entry : jobParametersMap.entrySet()) {
                    if(!Utils.isNullOrEmpty(entry.getKey()) && !Utils.isNullOrEmpty(entry.getValue())){
                        if(entry.getValue() instanceof String){
                            builder.addString(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                        if(entry.getValue() instanceof Double){
                            builder.addDouble(entry.getKey(), (Double) entry.getValue());
                        }
                        if(entry.getValue() instanceof Long){
                            builder.addLong(entry.getKey(), (Long) entry.getValue());
                        }
                        if(entry.getValue() instanceof LocalDate){
                            builder.addLocalDate(entry.getKey(), (LocalDate) entry.getValue());
                        }
                        if(entry.getValue() instanceof LocalDateTime){
                            builder.addLocalDateTime(entry.getKey(), (LocalDateTime) entry.getValue());
                        }
                    }
                }
            }
            builder.addString("JobID", String.valueOf(System.currentTimeMillis()));
            jobLauncher.run(job, builder.toJobParameters());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
