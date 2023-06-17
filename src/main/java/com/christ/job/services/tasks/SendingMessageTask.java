package com.christ.job.services.tasks;

import com.christ.job.services.common.*;
import com.christ.job.services.dbobjects.common.ErpSmsDBO;
import com.christ.job.services.dto.common.ErpSmsDTO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import jakarta.persistence.Tuple;
import okhttp3.internal.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendingMessageTask implements Tasklet {

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @Autowired
    private CommonApiTransaction transaction;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("--------------------start--------------------------------");
        System.out.println("Constants.SEND_SMS_COUNT : " + Constants.SEND_SMS_COUNT);
        if(Constants.SEND_SMS_COUNT == 0){
            sendSms();
        }
        System.out.println("--------------------end--------------------------------");
        return RepeatStatus.FINISHED;
    }

    public void sendSms() {
        try{
            Constants.SEND_SMS_COUNT = 1;
//            List<ErpSmsDBO> smsDBOList1 = transaction.getMessageList();
//            List<ErpSmsDBO> smsDBOList = new ArrayList<>();
//            List<Tuple> tupleList = transaction.getSms();
//            ErpSmsDBO erpSmsDBO1 = transaction.getsmsDBO();
//            smsDBOList.clear();
//            smsDBOList.add(erpSmsDBO1);
//            if(!Utils.isNullOrEmpty(smsDBOList)){
//                //List<ErpSmsDTO> erpSmsDTOS = SMSUtils.sendSMS(convertDBOToDTO(smsDBOList));
//                List<ErpSmsDTO> erpSmsDTOS = SMSUtil.sendMessageList(convertDBOToDTO(smsDBOList));
//                transaction.updateDBOS(convertDTOToDBO(erpSmsDTOS));
//            }
            List<ErpSmsDBO> smsDBOList = transaction.getsmsDBOs();
            if(!Utils.isNullOrEmpty(smsDBOList)){
                List<Object> erpSmsDbos = new ArrayList<>();
                for (ErpSmsDBO erpSmsDBO : smsDBOList) {
                    String messageStatus = "FAILED";
                    if(!Utils.isNullOrEmpty(erpSmsDBO.getRecipientMobileNo()) && !Utils.isNullOrEmpty(erpSmsDBO.getSmsContent()) && !Utils.isNullOrEmpty(erpSmsDBO.getTemplateId())){
                        String response = SMSUtil.sendMessage(erpSmsDBO.getRecipientMobileNo(), erpSmsDBO.getSmsContent(), erpSmsDBO.getTemplateId());
                        erpSmsDBO.setGatewayResponse(response);
                        System.out.println("--------------------------------------------------");
                        System.out.println("to mobile no "+erpSmsDBO.getRecipientMobileNo());
                        System.out.println("response : " + response);
                        if(!Utils.isNullOrEmpty(response)){
                            JSONObject data = (JSONObject) (new JSONParser()).parse(response);
                            if(!Utils.isNullOrEmpty(data)){
                                String status = (String) data.get("status");
                                if("OK".equalsIgnoreCase(status) || "200".equalsIgnoreCase(status)){
                                    if(!Utils.isNullOrEmpty((String) data.get("message"))){
                                        messageStatus = "SUCCESS";
                                        erpSmsDBO.setSmsIsSent(true);
                                    }
                                }
                            }
                        }
                    }
                    erpSmsDBO.setMessageStatus(messageStatus);
                    erpSmsDbos.add(erpSmsDBO);
                }
                transaction.updateDBOS(erpSmsDbos);
            }
            //or
            if(!Utils.isNullOrEmpty(smsDBOList)){
                List<Object> erpSmsDbos = SMSUtil.sendAllMessages(smsDBOList);
                if(!Utils.isNullOrEmpty(erpSmsDbos))
                    transaction.updateDBOS(erpSmsDbos);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Constants.SEND_SMS_COUNT = 0;
    }

    public static List<ErpSmsDTO> convertDBOToDTO(List<ErpSmsDBO> erpSmsDBOList){
        List<ErpSmsDTO> smsList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(erpSmsDBOList)){
            for (ErpSmsDBO erpSmsDBO : erpSmsDBOList) {
                ErpSmsDTO erpSmsDTO = new ErpSmsDTO();
                BeanUtils.copyProperties(erpSmsDBO, erpSmsDTO);
                smsList.add(erpSmsDTO);
            }
        }
        return smsList;
    }

    public static List<Object> convertDTOToDBO(List<ErpSmsDTO> erpSmsDTOS) {
        List<Object> erpSmsDBOList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(erpSmsDTOS)){
            for(ErpSmsDTO erpSmsDTO : erpSmsDTOS){
                ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
                BeanUtils.copyProperties(erpSmsDTO, erpSmsDBO);
                if(!Utils.isNullOrEmpty(erpSmsDTO.getSmsSentTime()))
                    erpSmsDBO.setSmsSentTime(erpSmsDTO.getSmsSentTime());
                if(erpSmsDTO.getMessageStatus().equalsIgnoreCase("SUCCESS")){
                    erpSmsDBO.setSmsIsSent(true);
                }
                erpSmsDBO.setRecordStatus('A');
                erpSmsDBOList.add(erpSmsDBO);
            }
        }
        return erpSmsDBOList;
    }

    public static List<ErpSmsDTO> convertDBOToDTO1(List<ErpSmsDBO> erpSmsDBOList){
        List<ErpSmsDTO> smsList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(erpSmsDBOList)){
            for (ErpSmsDBO erpSmsDBO : erpSmsDBOList) {
                ErpSmsDTO erpSmsDTO = new ErpSmsDTO();
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getId()))
//                    erpSmsDTO.setId(erpSmsDBO.getId());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getRecipientMobileNo()))
//                    erpSmsDTO.setRecipientMobileNo(erpSmsDBO.getRecipientMobileNo());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsContent()))
//                    erpSmsDTO.setSmsContent(erpSmsDBO.getSmsContent());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsIsSent()))
//                    erpSmsDTO.setSmsIsSent(String.valueOf(erpSmsDBO.getSmsIsSent()));
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsSentTime()))
//                    erpSmsDTO.setSmsSentTime(erpSmsDBO.getSmsSentTime().toString());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsIsDelivered()))
//                    erpSmsDTO.setSmsIsDelivered(erpSmsDBO.getSmsIsDelivered().toString());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getSmsDeliveredTime()))
//                    erpSmsDTO.setSmsDeliveredTime(erpSmsDBO.getSmsDeliveredTime().toString());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getMessageStatus()))
//                    erpSmsDTO.setMessageStatus(erpSmsDBO.getMessageStatus());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getTemplateId()))
//                    erpSmsDTO.setTemplateId(erpSmsDBO.getTemplateId());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getGatewayString()))
//                    erpSmsDTO.setGatewayString(erpSmsDBO.getGatewayString());
//                if(!Utils.isNullOrEmpty(erpSmsDBO.getSenderMobileNo()))
//                    erpSmsDTO.setSenderMobileNo(erpSmsDBO.getSenderMobileNo());
                BeanUtils.copyProperties(erpSmsDBO, erpSmsDTO);
                smsList.add(erpSmsDTO);
            }
        }
        return smsList;
    }

}
