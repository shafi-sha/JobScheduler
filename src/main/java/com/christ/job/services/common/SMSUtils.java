package com.christ.job.services.common;

import com.christ.job.services.dbobjects.common.ErpSmsDBO;
import com.christ.job.services.dto.common.ErpSmsDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SMSUtils {

//    @Autowired
//    private static RestTemplate restTemplate;
//
//    public SMSUtils(RestTemplate restTemplate){
//        SMSUtils.restTemplate = restTemplate;
//    }

    public static List<ErpSmsDTO> sendSMS(List<ErpSmsDTO> erpSmsDTOList){
        List<ErpSmsDTO> erpSmsDTOS = new ArrayList<>();
        for (ErpSmsDTO erpSms : erpSmsDTOList) {
            erpSmsDTOS.add(sendSMS(erpSms));
        }
        return erpSmsDTOS;
    }

    public static ErpSmsDTO sendSMS(ErpSmsDTO erpSmsDTO) {
        boolean netOrIOExceptionRaised = false;
        StringBuffer response = null;
        try{
            String formatRequestString = formatRequest(erpSmsDTO).toString();
            //response = sendRequest(formatRequestString);
            response = sendReq(formatRequestString);
        }catch(Throwable t){
            netOrIOExceptionRaised = true;
            t.printStackTrace();
        }
        return formatResponse(erpSmsDTO, response, netOrIOExceptionRaised);
    }

    public static StringBuffer formatRequest(ErpSmsDTO erpSmsDTO) throws Throwable {
        StringBuffer str = new StringBuffer();
        str.append(Constants.SMS_FILE_CFG);
        str.append(getNVP("to", erpSmsDTO.getRecipientMobileNo(), false));
        str.append(getNVP("message", erpSmsDTO.getSmsContent(), true));
        str.append(getNVP("template_id", erpSmsDTO.getTemplateId(), true));
        return str;
    }

    public static StringBuffer sendReq(String urlString) throws IOException, InterruptedException {
        StringBuffer responseBf = new StringBuffer();
//        ResponseEntity<String> response = restTemplate.exchange(urlString, HttpMethod.GET, null, String.class);
//        System.out.println("response : "+ response);
//        System.out.println("response getBody : "+ response.getBody());
//        System.out.println("response getStatusCode : "+ response.getStatusCode());
        //return responseBf.append(response.getBody());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response : "+ response);
        System.out.println("response body : "+ response.body());
        System.out.println("response statusCode : "+ response.statusCode());
        return responseBf.append(response.body());
    }

    public static StringBuffer getNVP(String name, String value, boolean encode) throws Exception {
        StringBuffer str = new StringBuffer();
        str.append(name).append("=");
        if (encode){
            str.append(URLEncoder.encode(value, "utf-8"));
        }else{
            str.append(value);
        }
        str.append("&");
        return str;
    }

    public static ErpSmsDTO formatResponse(ErpSmsDTO erpSmsDTO, StringBuffer response, boolean netOrIOException){
        if (netOrIOException){
            erpSmsDTO.setMessageStatus("RETRY");//retry
        }else{
            int c1 = response.indexOf("Code");
            if (c1 > 0){
                // Error returned
                erpSmsDTO.setMessageStatus("FAILED");

            }else{
                int x1 = response.indexOf("message");
                if (x1 < 0){
                    // Unknown Error from gateway
                    erpSmsDTO.setMessageStatus("FAILED");
                }else{
                    // Success
                    erpSmsDTO.setMessageStatus("SUCCESS");
                }
            }
        }
        erpSmsDTO.setSmsSentTime(LocalDateTime.now());
        erpSmsDTO.setGatewayResponse(response.toString());
        return erpSmsDTO;
    }
}
