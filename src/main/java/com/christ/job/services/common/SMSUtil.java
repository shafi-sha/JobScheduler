package com.christ.job.services.common;

import com.christ.job.services.dto.common.ErpSmsDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SMSUtil {

//    public static<T> List<T> sendMessageList(List<T> messageList) {
//        List<T> result = new ArrayList<>();
//        if(!Utils.isNullOrEmpty(messageList)){
//            messageList.forEach(t -> {
//
//            });
//        }
//        return result;
//    }

    public static ErpSmsDTO sendMessage(ErpSmsDTO erpSmsDTO) throws Exception{
        try {
            String response = sendRequest(formatSMSURIString(erpSmsDTO.getRecipientMobileNo(), erpSmsDTO.getSmsContent(), erpSmsDTO.getTemplateId()));
            if(!Utils.isNullOrEmpty(response)){
                String messageStatus = "FAILED";
                erpSmsDTO.setGatewayResponse(response);
                JSONObject data = (JSONObject) (new JSONParser()).parse(response);
                if(!Utils.isNullOrEmpty(data)){
                    String status = (String) data.get("status");
                    if("OK".equalsIgnoreCase(status) || "200".equalsIgnoreCase(status)){
                        if(!Utils.isNullOrEmpty((String) data.get("message"))){
                            messageStatus = "SUCCESS";
                        }
                    }
                }
                erpSmsDTO.setMessageStatus(messageStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return erpSmsDTO;
    }

    public static List<ErpSmsDTO> sendMessageList(List<ErpSmsDTO> messageList) throws Exception{
        List<ErpSmsDTO> erpSmsDTOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(messageList)){
            messageList.forEach(erpSmsDTO -> {
                try {
                    String response = sendRequest(formatSMSURIString(erpSmsDTO.getRecipientMobileNo(), erpSmsDTO.getSmsContent(), erpSmsDTO.getTemplateId()));
                    if(!Utils.isNullOrEmpty(response)){
                        String messageStatus = "FAILED";
                        erpSmsDTO.setGatewayResponse(response);
                        JSONObject data = (JSONObject) (new JSONParser()).parse(response);
                        if(!Utils.isNullOrEmpty(data)){
                            String status = (String) data.get("status");
                            if("OK".equalsIgnoreCase(status) || "200".equalsIgnoreCase(status)){
                                if(!Utils.isNullOrEmpty((String) data.get("message"))){
                                    messageStatus = "SUCCESS";
                                }
                            }
                        }
                        erpSmsDTO.setMessageStatus(messageStatus);
                    }
                    erpSmsDTOS.add(erpSmsDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return erpSmsDTOS;
    }

    public static String sendMessage(String to, String messageBody, String templateId) throws Exception{
        String messageStatus = "FAILED";
        try {
            String response = sendRequest(formatSMSURIString(to, messageBody, templateId));
            if(!Utils.isNullOrEmpty(response)){
                JSONObject data = (JSONObject) (new JSONParser()).parse(response);
                if(!Utils.isNullOrEmpty(data)){
                    String status = (String) data.get("status");
                    if("OK".equalsIgnoreCase(status) || "200".equalsIgnoreCase(status)){
                        if(!Utils.isNullOrEmpty((String) data.get("message"))){
                            messageStatus = "SUCCESS";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageStatus;
    }

    public static String formatSMSURIString(String to, String messageBody, String templateId) throws Exception{
        String uriString = UriComponentsBuilder.fromHttpUrl(Constants.SMS_FILE_CFG)
                .queryParam("to", to)
                .queryParam("message", URLEncoder.encode(messageBody, "utf-8"))
                .queryParam("template_id", templateId)
                .toUriString();
        return uriString;
    }

    public static String sendRequest(String uriString) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
