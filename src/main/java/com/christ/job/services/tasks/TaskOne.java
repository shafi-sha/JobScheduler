package com.christ.job.services.tasks;

import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskOne implements Tasklet {

  @Autowired
  private RedisSysPropertiesData redisSysPropertiesData;

  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    System.out.println();
    System.out.println();
    System.out.println("TaskOne start..");

    // ... your code
    //System.out.println(" data : " + redisSysPropertiesData.getSysProperties(SysProperties.UNIVERSITY_NAME.name(), "C", 1));

    System.out.println("TaskOne done..");
    System.out.println();
    System.out.println();
    return RepeatStatus.FINISHED;
  }

}