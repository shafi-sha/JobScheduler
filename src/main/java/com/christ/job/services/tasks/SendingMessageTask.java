package com.christ.job.services.tasks;

import com.christ.job.services.common.*;
import com.christ.job.services.dbobjects.common.ErpSmsDBO;
import com.christ.job.services.dto.common.ErpSmsDTO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(Constants.SEND_SMS_COUNT == 0){
            sendSms();
        }
        return RepeatStatus.FINISHED;
    }

    public void sendSms() {
        try{
            Constants.SEND_SMS_COUNT = 1;
            List<ErpSmsDBO> smsDBOList = new ArrayList<>();
            //List<ErpSmsDBO> smsDBOList = transaction.getMessageList();
            ErpSmsDBO erpSmsDBO = transaction.getsmsDBO();
            smsDBOList.clear();
            smsDBOList.add(erpSmsDBO);
            if(!Utils.isNullOrEmpty(smsDBOList)){
                //List<ErpSmsDTO> erpSmsDTOS = SMSUtils.sendSMS(convertDBOToDTO(smsDBOList));
                List<ErpSmsDTO> erpSmsDTOS = SMSUtil.sendMessageList(convertDBOToDTO(smsDBOList));
                transaction.updateDBOS(convertDTOToDBO(erpSmsDTOS));
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
