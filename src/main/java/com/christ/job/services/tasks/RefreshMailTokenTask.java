package com.christ.job.services.tasks;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpEmailsDBO;
import com.christ.job.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.caching.CacheUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RefreshMailTokenTask implements Tasklet {

    @Autowired
    private CommonApiTransaction commonApiTransaction;

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Override
    public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) throws Exception {
        setMailRefreshToken();
        return RepeatStatus.FINISHED;
    }

    private void setMailRefreshToken() {
        Map<String, String> emailTokenMap = new HashMap<>();
        try{
            System.out.println("--------------RefreshMailTokenTask-------------------");
            List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOS = commonApiTransaction.getEmailSenderSettings();
            if(!Utils.isNullOrEmpty(erpNotificationEmailSenderSettingsDBOS)){
                List<Object> notificationEmailSenderSettingsDBOS = new ArrayList<>();
                for (ErpNotificationEmailSenderSettingsDBO erpNotificationEmailSenderSettingsDBO : erpNotificationEmailSenderSettingsDBOS) {
                    emailTokenMap.put(erpNotificationEmailSenderSettingsDBO.getToken(), erpNotificationEmailSenderSettingsDBO.getSenderEmail());
                    String uriString = new DefaultUriBuilderFactory().builder()
                            .queryParam("client_id", URLEncoder.encode(erpNotificationEmailSenderSettingsDBO.getClientId(), "utf-8"))
                            .queryParam("client_secret", URLEncoder.encode(erpNotificationEmailSenderSettingsDBO.getClientSecret(), "utf-8"))
                            .queryParam("refresh_token", URLEncoder.encode(erpNotificationEmailSenderSettingsDBO.getRefreshToken(), "utf-8"))
                            .queryParam("grant_type", "refresh_token")
                            .build().getQuery();
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(Constants.EMAIL_REFRESH_TOKEN_URL))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.ofString(uriString))
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if(!Utils.isNullOrEmpty(response) && !Utils.isNullOrEmpty(response.body())){
                        JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);
                        if(!Utils.isNullOrEmpty(jsonObject) && !Utils.isNullOrEmpty(jsonObject.get("access_token"))){
                            String accessToken = jsonObject.get("access_token").getAsString();
                            System.out.println("---------------------------------");
                            System.out.println("accessToken : " + accessToken);
                            System.out.println("id : " + erpNotificationEmailSenderSettingsDBO.getId());
                            erpNotificationEmailSenderSettingsDBO.setToken(accessToken);
                        }
                    }
                    notificationEmailSenderSettingsDBOS.add(erpNotificationEmailSenderSettingsDBO);
                }
                commonApiTransaction.updateDBOS(notificationEmailSenderSettingsDBOS);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(e.toString().contains("jakarta.mail.MessagingException: 334") || e.toString().contains("334")){
                //send mail to erp support- recepient mail should be sys_properties
                String[] exceptionString = e.toString().split(" ");
                String senderMail = "";
                for (String word : exceptionString) {
                    //senderMail = String.valueOf(emailTokenMap.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(word)).findFirst());
                    senderMail = String.valueOf(emailTokenMap.keySet().stream().filter(s -> s.equalsIgnoreCase(word)).findFirst());
                    if(!Utils.isNullOrEmpty(senderMail)){
                        System.out.println("senderMail(refresh token exc) : "+senderMail);
                        break;
                    }
                }
                if(!Utils.isNullOrEmpty(senderMail))
                    sendIntimationToERP(senderMail);
            }
        }
    }

    public synchronized void sendIntimationToERP(String userName){
        ErpEmailsDBO emailsDBO = new ErpEmailsDBO();
        emailsDBO.setSenderName("Christ University");
        emailsDBO.setEmailContent("Refresh token and Token has been expired for emailId: "+ userName);
        emailsDBO.setEmailSubject("Refresh Token Expired");
        emailsDBO.setPriorityLevelOrder(2);
        emailsDBO.setEmailIsSent(false);
        emailsDBO.setRecipientEmail(redisSysPropertiesData.getSysProperties(SysProperties.ERP_INTIMATION_EMAIL.name(), null, null));
        emailsDBO.setRecordStatus('A');
        commonApiTransaction.saveErpEmailsDBO(emailsDBO);
    }

    public void sendIntimationToERP1(String emailContent, String emailSubject, String senderName, Integer priorityLevelOrder, String userName, String exceptionName){
        boolean isEmailSendRequired = true;
        String emailExceptionData = CacheUtils.instance.get("__priority_failed_emails_map_", userName);
        System.out.println("sendIntimationToERP emailExceptionData");
        if(!Utils.isNullOrEmpty(emailExceptionData)){
            long day = Duration.between(LocalDateTime.parse(emailExceptionData.split("_")[1]), LocalDateTime.now()).toDays();
            System.out.println("sendIntimationToERP day : "+day);
            if(day < 1){
                isEmailSendRequired = false;
            }
        } else {
            System.out.println("sendIntimationToERP emailExceptionData");
            CacheUtils.instance.set("__priority_failed_emails_map_", userName, exceptionName +"_"+ (LocalDateTime.now()));
        }
        if(isEmailSendRequired){
            System.out.println("sendIntimationToERP email send");
            ErpEmailsDBO emailsDBO = new ErpEmailsDBO();
            emailsDBO.setSenderName(senderName);
            emailsDBO.setEmailContent(emailContent);
            emailsDBO.setEmailSubject(emailSubject);
            emailsDBO.setPriorityLevelOrder(priorityLevelOrder);
            emailsDBO.setEmailIsSent(false);
            emailsDBO.setRecipientEmail(redisSysPropertiesData.getSysProperties(SysProperties.ERP_INTIMATION_EMAIL.name(), null, null));
            emailsDBO.setRecordStatus('A');
            commonApiTransaction.saveErpEmailsDBO(emailsDBO);
        }
    }
}
