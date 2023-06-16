package com.christ.job.services.tasks;

import com.christ.job.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RefreshMailTokenTask implements Tasklet {


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        setMailRefreshToken();
        return RepeatStatus.FINISHED;
    }

    private void setMailRefreshToken() {
        List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOS = new ArrayList<>();
    }
}
