package com.christ.job.services.tasks;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.SMSUtil;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpSmsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendMessageTask implements Tasklet {

    @Autowired
    private CommonApiTransaction transaction;

    @Override
    public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) throws Exception {
        System.out.println("--------------------start--------------------------------");
        System.out.println("Constants.SEND_SMS_COUNT : " + Constants.SEND_SMS_COUNT);
        if(Constants.SEND_SMS_COUNT == 0){
            sendSms();
        }
        System.out.println("--------------------end--------------------------------");
        return RepeatStatus.FINISHED;
    }

    public void sendSms() {
        try{
            Constants.SEND_SMS_COUNT = 1;
            List<ErpSmsDBO> smsDBOList = transaction.getsmsDBOs("UNDELIV");
            if(!Utils.isNullOrEmpty(smsDBOList)){
                List<Object> erpSmsDBOs = SMSUtil.sendAllMessages(smsDBOList);
                if(!Utils.isNullOrEmpty(erpSmsDBOs))
                    transaction.updateDBOS(erpSmsDBOs);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Constants.SEND_SMS_COUNT = 0;
    }
}
