package com.christ.job.services.tasks;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpEmailsDBO;
import com.christ.job.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import com.christ.job.services.utilities.MailMessageWorker;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class SendMailTask implements Tasklet {

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Autowired
    private CommonApiTransaction commonApiTransaction;

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

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
                Map<Integer, Map<String, String>> emailSenderMap = erpNotificationEmailSenderSettingsDBOList.stream()
                        .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder, Collectors.toMap(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, ErpNotificationEmailSenderSettingsDBO::getToken)));
                Constants.SEND_MAIL_COUNT = 1;
                List<ErpEmailsDBO> erpEmailsDBOS = commonApiTransaction.getErpEmailsDBOs();
                if(!Utils.isNullOrEmpty(erpEmailsDBOS)){
                    int count = Constants.LAST_MAIL_SEND_COUNT;
                    int mailSendCount = 1;
                    System.out.println("----------------------------");
                    System.out.println("recipient emails size : " + erpEmailsDBOS.size());
                    System.out.println("LAST_MAIL_SEND_COUNT before: "+ count);
                    int priorityMailsSize = emailSenderMap.get(2).size();
                    System.out.println("priorityMailsSize size : "+ priorityMailsSize);
                    ExecutorService executorService = Executors.newFixedThreadPool(priorityMailsSize);
                    int threadCount = 0;
                    for (ErpEmailsDBO erpEmailsDBO : erpEmailsDBOS) {
                        try{
                            if(!Utils.isNullOrEmpty(erpEmailsDBO.getRecipientEmail())){
                                System.out.println("-------------mail "+mailSendCount+ " : " + erpEmailsDBO.getRecipientEmail() +" ---------------");
                                erpEmailsDBO.setEmailContent(erpEmailsDBO.getEmailContent() + " mailSendCount : " + mailSendCount);
                                mailSendCount++;
                                Constants.MAIL_USERNAME = MailMessageWorker.getUserNameByPriorityOrderForEmail(count, erpEmailsDBO.getPriorityLevelOrder());
                                String token = emailSenderMap.get(erpEmailsDBO.getPriorityLevelOrder()).get(Constants.MAIL_USERNAME);
                                System.out.println("threadCount : "+ threadCount);
                                System.out.println("MAIL_USERNAME : "+ Constants.MAIL_USERNAME);
                                System.out.println("Token : "+ token);
                                if (count == (priorityMailsSize - 1)) {
                                    count = 0;
                                } else {
                                    count++;
                                }
                                Constants.LAST_MAIL_SEND_COUNT = count;
                                System.out.println("LAST_MAIL_SEND_COUNT after: "+ count);
                                if (!Utils.isNullOrEmpty(Constants.MAIL_USERNAME)) {
                                    Runnable mailWorker = new MailMessageWorker(Constants.MAIL_USERNAME, token, erpEmailsDBO.getPriorityLevelOrder(), erpEmailsDBO, sessionFactory, redisSysPropertiesData);
                                    executorService.execute(mailWorker);
                                    threadCount++;
                                    if(threadCount == (priorityMailsSize - 1)){
                                        threadCount = 0;
                                        Thread.sleep(2000);
                                    }
                                }
                            }
                            System.out.println("----------------------------");
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Exception");
                        }
                    }
                    executorService.shutdown();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception : "+e.getMessage());
        }
        Constants.SEND_MAIL_COUNT = 0;
    }

}
