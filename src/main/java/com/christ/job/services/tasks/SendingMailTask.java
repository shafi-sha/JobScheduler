package com.christ.job.services.tasks;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import com.christ.job.services.common.Utils;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SendingMailTask implements Tasklet {

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Override
    public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) throws Exception {
        if(Constants.SEND_MAIL_COUNT == 0){
            sendMail();
        }
        return RepeatStatus.FINISHED;
    }

    public void sendMail() {
        try{
            String userMails = redisSysPropertiesData.getSysProperties(SysProperties.USER_MAILS.name(), null, null);
            String[] userMails1;
            if(!Utils.isNullOrEmpty(userMails)){
                userMails1 = userMails.split(",");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
