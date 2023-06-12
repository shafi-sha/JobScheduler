package com.christ.job.services.common;

import com.christ.job.services.dbobjects.common.ErpSmsDBO;
import com.christ.job.services.dto.common.ErpSmsDTO;
import org.springframework.beans.BeanUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SMSUtils {


    public static List<ErpSmsDTO> convertDBOToDTO(List<ErpSmsDBO> erpSmsDBOList){
        List<ErpSmsDTO> smsList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(erpSmsDBOList)){
            for (ErpSmsDBO erpSmsDBO : erpSmsDBOList) {
                ErpSmsDTO erpSmsDTO = new ErpSmsDTO();
                erpSmsDTO.setId(erpSmsDBO.getId());
                if(!Utils.isNullOrEmpty(erpSmsDBO.getRecipientMobileNo()))
                    erpSmsDTO.setRecipientMobileNo(erpSmsDBO.getRecipientMobileNo());
                //smsMessageDTO.setMessageEnqueueDate(erpSmsDBO.getMessageEnqueueDate());
                if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsContent()))
                    erpSmsDTO.setSmsContent(erpSmsDBO.getSmsContent());
                //smsMessageDTO.setMessage_id(erpSmsDBO.getId());
//                if(erpSmsDBO.getMessagePriority()!=null && !erpSmsDBO.getMessagePriority().equals("") && !erpSmsDBO.getMessagePriority().equals(" "))
//                    smsMessageDTO.setMessage_priority(Integer.toString(erpSmsDBO.getMessagePriority()));
//                if(erpSmsDBO.getMessageStatus()!=null && !erpSmsDBO.getMessageStatus().isEmpty() && erpSmsDBO.getMessageStatus().length() > 0)
//                    smsMessageDTO.setMessage_status(Integer.parseInt(erpSmsDBO.getMessageStatus()));
//                erpSmsDTO.setSenderName(erpSmsDBO.getSenderName());
                if(!Utils.isNullOrEmpty(erpSmsDBO.getSenderMobileNo()))
                    erpSmsDTO.setSenderMobileNo(erpSmsDBO.getSenderMobileNo());
                smsList.add(erpSmsDTO);
            }
        }
        return smsList;
    }

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
            response = sendRequest(formatRequestString);
        }catch(Throwable t){
            netOrIOExceptionRaised = true;
            t.printStackTrace();
        }
        return formatResponse(erpSmsDTO, response, netOrIOExceptionRaised);
    }

    public static StringBuffer sendRequest(String urlString)
            throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(40000);
        byte buffer[] = new byte[8192];
        int read = 0;
        // do request
        long time = System.currentTimeMillis();
        connection.connect();
        InputStream responseBodyStream = connection.getInputStream();
        StringBuffer responseBody = new StringBuffer();
        while ((read = responseBodyStream.read(buffer)) != -1) {
            responseBody.append(new String(buffer, 0, read));
        }
        connection.disconnect();
        //time = System.currentTimeMillis() - time;
        return responseBody;
    }

    public static StringBuffer formatRequest(ErpSmsDTO erpSmsDTO) throws Throwable {
        StringBuffer str = new StringBuffer();
        str.append(Constants.SMS_FILE_CFG);
        str.append(getNVP("to", erpSmsDTO.getRecipientMobileNo(), false));
        str.append(getNVP("message", erpSmsDTO.getSmsContent(), true));
        return str;
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
        // Process error if any
        if (netOrIOException){
            //erpSmsDTO.setMessage_status(SMS_Message.MESSAGE_RETRY);
        }else{
            int c1 = response.indexOf("Code");
            if (c1 > 0){
                // Error returned
                //erpSmsDTO.setSms_gateway_response(response.toString());
                //erpSmsDTO.setMessage_status(SMS_Message.MESSAGE_FAIL);

            }else{
                // MessageSent GUID="12143003" SUBMITDATE="7/15/2010 2:55:11 PM"
                int x1 = response.indexOf("Message");
                if (x1 < 0){
                    // Unknown Error from gateway
                    //erpSmsDTO.setSms_gateway_response(response.toString());
                    //erpSmsDTO.setMessage_status(SMS_Message.MESSAGE_FAIL);
                }else{
                    // Success
                    int x3 = response.indexOf("GID=");
                    int x4 = x3 +"GID=".length();
                    int x5 = response.indexOf(" ", x4);

                    String guid = response.substring(x4, x5);
                    //erpSmsDTO.setSms_guid(guid);
                    //erpSmsDTO.setSms_gateway_response(response.toString());
                    //erpSmsDTO.setMessageStatus(SMS_Message.MESSAGE_SUCCESS);
                }

            }
        }
        return erpSmsDTO;

    }

    public static List<ErpSmsDBO> convertDTOToDBO(List<ErpSmsDTO> erpSmsDTOS) {
        List<ErpSmsDBO> erpSmsDBOList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(erpSmsDTOS)){
            for(ErpSmsDTO erpSmsDTO : erpSmsDTOS){
                if(erpSmsDTO.getMessageStatus() != 2){
                    ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
                    BeanUtils.copyProperties(erpSmsDTO, erpSmsDBO);
                    erpSmsDBO.setSmsIsSent(true);
                    erpSmsDBOList.add(erpSmsDBO);
                }
            }
        }
        return erpSmsDBOList;
    }
}
