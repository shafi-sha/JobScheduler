package com.christ.job.services.tasks;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
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
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class RefreshMailTokenTask implements Tasklet {

    @Autowired
    private CommonApiTransaction transaction;

    @Override
    public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) throws Exception {
        setMailRefreshToken();
        return RepeatStatus.FINISHED;
    }

    private void setMailRefreshToken() {
        try{
            List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOS = transaction.getEmailSenderSettings();
            erpNotificationEmailSenderSettingsDBOS.clear();
            ErpNotificationEmailSenderSettingsDBO notificationEmailSenderSettingsDBO = transaction.getEmailSenderSettingDBO();
            erpNotificationEmailSenderSettingsDBOS.add(notificationEmailSenderSettingsDBO);
            if(!Utils.isNullOrEmpty(erpNotificationEmailSenderSettingsDBOS)){
                List<Object> notificationEmailSenderSettingsDBOS = new ArrayList<>();
                for (ErpNotificationEmailSenderSettingsDBO erpNotificationEmailSenderSettingsDBO : erpNotificationEmailSenderSettingsDBOS) {
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
                transaction.updateDBOS(notificationEmailSenderSettingsDBOS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
