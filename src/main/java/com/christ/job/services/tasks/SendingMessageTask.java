package com.christ.job.services.tasks;

import com.christ.job.services.common.*;
import com.christ.job.services.dbobjects.common.ErpSmsDBO;
import com.christ.job.services.dto.common.ErpSmsDTO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendingMessageTask implements Tasklet {

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Autowired
    private CommonApiTransaction transaction;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if(Constants.SEND_SMS_COUNT == 0){
            sendSms();
        }
        return RepeatStatus.FINISHED;
    }

    public void sendSms() {
        try{
            Constants.SEND_SMS_COUNT = 1;
            List<ErpSmsDBO> smsDBOList = transaction.getMessageList();
            ErpSmsDBO erpSmsDBO = transaction.getsmsDBO();
            smsDBOList.clear();
            smsDBOList.add(erpSmsDBO);
            if(!Utils.isNullOrEmpty(smsDBOList)){
                List<ErpSmsDTO> erpSmsDTOS = SMSUtils.sendSMS(SMSUtils.convertDBOToDTO(smsDBOList));
                transaction.updateSMS(SMSUtils.convertDTOToDBO(erpSmsDTOS));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Constants.SEND_SMS_COUNT = 0;
    }
}
