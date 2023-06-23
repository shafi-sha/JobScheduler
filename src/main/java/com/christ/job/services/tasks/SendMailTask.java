package com.christ.job.services.tasks;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpEmailsDBO;
import com.christ.job.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import com.christ.job.services.utilities.MailMessageWorker;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SendMailTask implements Tasklet {

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Autowired
    private CommonApiTransaction commonApiTransaction;

    @Override
    public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) throws Exception {
        if(Constants.SEND_MAIL_COUNT == 0){
            sendMail();
        }
        return RepeatStatus.FINISHED;
    }

    public void sendMail() {
        try{
            //get mail sender settings
            List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOList = commonApiTransaction.getEmailSenderSettings();
            if(!Utils.isNullOrEmpty(erpNotificationEmailSenderSettingsDBOList)){
//                Map<Integer, List<ErpNotificationEmailSenderSettingsDBO>> emailSenderMap = erpNotificationEmailSenderSettingsDBOList.stream().collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder));
//                Map<Integer, Map<String, ErpNotificationEmailSenderSettingsDBO>> emailSendMap = erpNotificationEmailSenderSettingsDBOList.stream().collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,Collectors.toMap(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, o-> o)));
                Map<Integer, Map<String, String>> emailSenderMap = erpNotificationEmailSenderSettingsDBOList.stream()
                        .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder, Collectors.toMap(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, ErpNotificationEmailSenderSettingsDBO::getToken)));
                Constants.SEND_MAIL_COUNT = 1;
                String userMails = redisSysPropertiesData.getSysProperties(SysProperties.USER_MAILS.name(), null, null);
                System.out.println("userMails : " + userMails);
                String[] userEmails;
                if(!Utils.isNullOrEmpty(userMails)){
                    userEmails = userMails.split(",");
                }
                List<ErpEmailsDBO> erpEmailsDBOS = commonApiTransaction.getErpEmailsDBOs();
                if(!Utils.isNullOrEmpty(erpEmailsDBOS)){
                    int count = Constants.LAST_MAIL_SEND_COUNT;
                    System.out.println("----------------------------");
                    System.out.println("LAST_MAIL_SEND_COUNT before: "+ count);
                    ExecutorService executorService = Executors.newFixedThreadPool(4);
                    int threadCount = 0;
                    for (ErpEmailsDBO erpEmailsDBO : erpEmailsDBOS) {
                        try{
                            if(!Utils.isNullOrEmpty(erpEmailsDBO.getRecipientEmail())){
                                Constants.MAIL_USERNAME = MailMessageWorker.getUserNameByPriorityOrderForEmail(null, count, erpEmailsDBO.getPriorityLevelOrder());
                                System.out.println("MAIL_USERNAME : "+ Constants.MAIL_USERNAME);
                                System.out.println("MAIL_PASSWORD: "+ redisSysPropertiesData.getSysProperties(SysProperties.MAIL_PASSWORD.name(), null, null));
                                System.out.println("Token : "+ emailSenderMap.get(erpEmailsDBO.getPriorityLevelOrder()).get(Constants.MAIL_USERNAME));
                                System.out.println("USER_MAILS_1 size : "+ Constants.USER_MAILS_1.size());
                                if (count == (Constants.USER_MAILS_1.size() - 1)) {
                                    count = 0;
                                } else {
                                    count++;
                                }
                                Constants.LAST_MAIL_SEND_COUNT = count;
                                System.out.println("LAST_MAIL_SEND_COUNT after: "+ count);
                                if (!Utils.isNullOrEmpty(Constants.MAIL_USERNAME)) {
                                    Runnable mailWorker = new MailMessageWorker(Constants.MAIL_USERNAME, redisSysPropertiesData.getSysProperties(SysProperties.MAIL_PASSWORD.name(), null, null),
                                            emailSenderMap.get(erpEmailsDBO.getPriorityLevelOrder()).get(Constants.MAIL_USERNAME), erpEmailsDBO.getPriorityLevelOrder(), erpEmailsDBO,
                                            redisSysPropertiesData, commonApiTransaction);
                                    executorService.execute(mailWorker);
                                    threadCount++;
                                    if(threadCount == Constants.USER_MAILS_1.size()){
                                        threadCount = 0;
                                        Thread.sleep(5000);
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Exception");
                        }
                    }
                    executorService.shutdown();
                    executorService.awaitTermination(60, TimeUnit.SECONDS);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception");
        }
        Constants.SEND_MAIL_COUNT = 0;
    }

}
