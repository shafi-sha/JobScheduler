package com.christ.job.services.tasks;


import com.christ.job.services.common.Utils;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import jakarta.persistence.Tuple;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskTwo implements Tasklet {

  @Autowired
  CommonApiTransaction commonApiTransaction;

  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    System.out.println();
    System.out.println();
    System.out.println("TaskTwo start..");

    List<Tuple> erpProperties = commonApiTransaction.getERPProperties();
    if(!Utils.isNullOrEmpty(erpProperties)) {
      System.out.println("TaskTwo redis data working");
    }

    System.out.println("TaskTwo done..");
    System.out.println();
    System.out.println();
    return RepeatStatus.FINISHED;
  }
}
