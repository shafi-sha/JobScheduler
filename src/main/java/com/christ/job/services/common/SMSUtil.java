package com.christ.job.services.common;

import com.christ.job.services.dbobjects.common.ErpSmsDBO;
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
import java.time.format.DateTimeFormatter;
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

    public static String formatSMSURIString(String to, String messageBody, String templateId) throws Exception{
        String uriString = UriComponentsBuilder.fromHttpUrl(Constants.SMS_SEND_URL)
                .queryParam("to", to)
                .queryParam("message", URLEncoder.encode(messageBody, "utf-8"))
                .queryParam("template_id", templateId)
                .toUriString();
        return uriString;
    }

    public static String formatSMSStatusCheckURIString(String id) throws Exception{
        String uriString = UriComponentsBuilder.fromHttpUrl(Constants.SMS_SEND_STATUS_URL)
                .queryParam("id", id)
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

    public static ErpSmsDTO sendMessageDTO(ErpSmsDTO erpSmsDTO) throws Exception{
        try {
            String response = sendRequest(formatSMSURIString(erpSmsDTO.getRecipientMobileNo(), erpSmsDTO.getSmsContent(), erpSmsDTO.getTemplateId()));
            if(!Utils.isNullOrEmpty(response)){
                String messageStatus = "SEND-FAILED";
                erpSmsDTO.setGatewayResponse(response);
                JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                if(!Utils.isNullOrEmpty(responseData)){
                    String responseStatus = (String) responseData.get("status");
                    if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                        JSONArray data = (JSONArray) responseData.get("data");
                        if(!Utils.isNullOrEmpty(data)){
                            for (Object dataItem : data) {
                                JSONObject item = (JSONObject) dataItem;
                                if(!Utils.isNullOrEmpty(item)){
                                    String status = (String) item.get("status");
                                    String smsTransactionId = (String) item.get("id");
                                    if(!Utils.isNullOrEmpty(smsTransactionId))
                                        erpSmsDTO.setSmsTransactionId(smsTransactionId);
                                    if("AWAITED-DLR".equalsIgnoreCase(status)){
                                        erpSmsDTO.setSmsIsSent(true);
                                        erpSmsDTO.setSmsSentTime(LocalDateTime.now());
                                        messageStatus = status;//or waiting
                                    } else if("INVALID-NUM".equalsIgnoreCase(status)){
                                        messageStatus = status;
                                    }
                                }
                            }
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

    public static List<ErpSmsDTO> sendMessageListDTOs(List<ErpSmsDTO> messageList) throws Exception{
        List<ErpSmsDTO> erpSmsDTOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(messageList)){
            messageList.forEach(erpSmsDTO -> {
                try {
                    String response = sendRequest(formatSMSURIString(erpSmsDTO.getRecipientMobileNo(), erpSmsDTO.getSmsContent(), erpSmsDTO.getTemplateId()));
                    if(!Utils.isNullOrEmpty(response)){
                        String messageStatus = "SEND-FAILED";
                        erpSmsDTO.setGatewayResponse(response);
                        JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                        if(!Utils.isNullOrEmpty(responseData)){
                            String responseStatus = (String) responseData.get("status");
                            if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                                JSONArray data = (JSONArray) responseData.get("data");
                                if(!Utils.isNullOrEmpty(data)){
                                    for (Object dataItem : data) {
                                        JSONObject item = (JSONObject) dataItem;
                                        if(!Utils.isNullOrEmpty(item)){
                                            String status = (String) item.get("status");
                                            String smsTransactionId = (String) item.get("id");
                                            if(!Utils.isNullOrEmpty(smsTransactionId))
                                                erpSmsDTO.setSmsTransactionId(smsTransactionId);
                                            if("AWAITED-DLR".equalsIgnoreCase(status)){
                                                messageStatus = status;
                                                erpSmsDTO.setSmsIsSent(true);
                                                erpSmsDTO.setSmsSentTime(LocalDateTime.now());
                                            } else if("INVALID-NUM".equalsIgnoreCase(status)){
                                                messageStatus = status;
                                            }
                                        }
                                    }
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

//    public static String sendMessage(String to, String messageBody, String templateId) throws Exception{
//        String messageStatus = "FAILED";
//        try {
//            String response = sendRequest(formatSMSURIString(to, messageBody, templateId));
//            if(!Utils.isNullOrEmpty(response)){
//                JSONObject data = (JSONObject) (new JSONParser()).parse(response);
//                if(!Utils.isNullOrEmpty(data)){
//                    String status = (String) data.get("status");
//                    if("OK".equalsIgnoreCase(status) || "200".equalsIgnoreCase(status)){
//                        if(!Utils.isNullOrEmpty((String) data.get("message"))){
//                            messageStatus = "SUCCESS";
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return messageStatus;
//    }

    public static String sendMessage(String to, String messageBody, String templateId) throws Exception{
        return sendRequest(formatSMSURIString(to, messageBody, templateId));
    }

    public static List<Object> sendAllMessages(List<ErpSmsDBO> messageList) throws Exception{
        List<Object> erpSmsDBOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(messageList)){
            messageList.forEach(erpSmsDBO -> {
                try {
                    if(!Utils.isNullOrEmpty(erpSmsDBO.getRecipientMobileNo()) && !Utils.isNullOrEmpty(erpSmsDBO.getSmsContent()) && !Utils.isNullOrEmpty(erpSmsDBO.getTemplateId())){
                        String response = sendRequest(formatSMSURIString(erpSmsDBO.getRecipientMobileNo(), erpSmsDBO.getSmsContent(), erpSmsDBO.getTemplateId()));
                        if(!Utils.isNullOrEmpty(response)){
                            String messageStatus = "SEND-FAILED";
                            erpSmsDBO.setGatewayResponse(response);
                            JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                            if(!Utils.isNullOrEmpty(responseData)){
                                String responseStatus = (String) responseData.get("status");
                                if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                                    JSONArray data = (JSONArray) responseData.get("data");
                                    if(!Utils.isNullOrEmpty(data)){
                                        for (Object dataItem : data) {
                                            JSONObject item = (JSONObject) dataItem;
                                            if(!Utils.isNullOrEmpty(item)){
                                                String status = (String) item.get("status");
                                                String smsTransactionId = (String) item.get("id");
                                                if(!Utils.isNullOrEmpty(smsTransactionId))
                                                    erpSmsDBO.setSmsTransactionId(smsTransactionId);
                                                if(!Utils.isNullOrEmpty(status)) {
                                                    if ("AWAITED-DLR".equalsIgnoreCase(status)) {
                                                        messageStatus = status;
                                                        erpSmsDBO.setSmsIsSent(true);
                                                        erpSmsDBO.setSmsSentTime(LocalDateTime.now());
                                                    } else if ("INVALID-NUM".equalsIgnoreCase(status) || "INV-NUMBER".equalsIgnoreCase(status)) {
                                                        messageStatus = status;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            erpSmsDBO.setMessageStatus(messageStatus);
                        }
                    }
                    erpSmsDBOS.add(erpSmsDBO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return erpSmsDBOS;
    }

    public static List<Object> checkAllMessageStatus(List<ErpSmsDBO> smsDBOList) throws Exception{
        List<Object> erpSmsDBOS = new ArrayList<>();
        if(!Utils.isNullOrEmpty(smsDBOList)){
            smsDBOList.forEach(erpSmsDBO -> {
                try {
                    if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsTransactionId())){
                        String response = sendRequest(formatSMSStatusCheckURIString(erpSmsDBO.getSmsTransactionId()));
                        if(!Utils.isNullOrEmpty(response)){
                            erpSmsDBO.setGatewayResponse(response);
                            JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                            if(!Utils.isNullOrEmpty(responseData)){
                                String responseStatus = (String) responseData.get("status");
                                if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                                    JSONArray data = (JSONArray) responseData.get("data");
                                    if(!Utils.isNullOrEmpty(data)){
                                        for (Object dataItem : data) {
                                            JSONObject item = (JSONObject) dataItem;
                                            if(!Utils.isNullOrEmpty(item)){
                                                String status = (String) item.get("status");
                                                if(!Utils.isNullOrEmpty(status)){
                                                    if("DELIVRD".equalsIgnoreCase(status)){
                                                        erpSmsDBO.setSmsIsDelivered(true);
                                                        String deliveryTime = (String) item.get("dlrtime");
                                                        if(!Utils.isNullOrEmpty(deliveryTime))
                                                            erpSmsDBO.setSmsDeliveredTime(Utils.convertStringLocalDateTimeToLocalDateTime(deliveryTime));
                                                    }
                                                    erpSmsDBO.setMessageStatus(status);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    erpSmsDBOS.add(erpSmsDBO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return erpSmsDBOS;
    }

    public static Object checkMessageStatus(ErpSmsDBO erpSmsDBO) throws Exception{
        if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsTransactionId())){
            try {
                String response = sendRequest(formatSMSStatusCheckURIString(erpSmsDBO.getSmsTransactionId()));
                if(!Utils.isNullOrEmpty(response)){
                    erpSmsDBO.setGatewayResponse(response);
                    JSONObject responseData = (JSONObject) (new JSONParser()).parse(response);
                    if(!Utils.isNullOrEmpty(responseData)){
                        String responseStatus = (String) responseData.get("status");
                        if("OK".equalsIgnoreCase(responseStatus) || "200".equalsIgnoreCase(responseStatus)){
                            JSONArray data = (JSONArray) responseData.get("data");
                            if(!Utils.isNullOrEmpty(data)){
                                for (Object dataItem : data) {
                                    JSONObject item = (JSONObject) dataItem;
                                    if(!Utils.isNullOrEmpty(item)){
                                        String status = (String) item.get("status");
                                        if(!Utils.isNullOrEmpty(status)){
                                            if("DELIVRD".equalsIgnoreCase(status)){
                                                erpSmsDBO.setSmsIsDelivered(true);
                                                String deliveryTime = (String) item.get("dlrtime");
                                                if(!Utils.isNullOrEmpty(deliveryTime))
                                                    erpSmsDBO.setSmsDeliveredTime(Utils.convertStringLocalDateTimeToLocalDateTime(deliveryTime));
                                            }
                                            erpSmsDBO.setMessageStatus(status);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return erpSmsDBO;
    }
}
