package com.christ.job.services.common;

import com.christ.job.services.dbobjects.common.ErpNotificationEmailSenderSettingsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmailCacheUtils {

    @Autowired
    private CommonApiTransaction commonApiTransaction;

    @Autowired
    private RedisSysPropertiesData redisSysPropertiesData;

    @PostConstruct
    public void setUserEmails() {
        List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOList = commonApiTransaction.getEmailSenderSettings();
        if(!Utils.isNullOrEmpty(erpNotificationEmailSenderSettingsDBOList)) {
            Map<Integer, Map<String, String>> emailSenderMap = erpNotificationEmailSenderSettingsDBOList.stream()
                    .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,
                            Collectors.toMap(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, ErpNotificationEmailSenderSettingsDBO::getToken)));
            List<String> userMails = new LinkedList<>(Arrays.asList(redisSysPropertiesData.getSysProperties(SysProperties.USER_MAILS.name(), null, null).split(",")));
            if(Utils.isNullOrEmpty(userMails)){
                userMails = Arrays.asList(commonApiTransaction.getUserEmail("USER_MAILS").split(","));
            }
            if(!Utils.isNullOrEmpty(userMails) && !Utils.isNullOrEmpty(emailSenderMap)){
                List<String> userMails1 = new ArrayList<>(userMails.subList(0, userMails.size()/2));
                List<String> userMails2 = new ArrayList<>(userMails.subList(userMails.size()/2, userMails.size()));
                Map<Integer, LinkedList<String>> userMailsByPriorityOrderMap1 = new LinkedHashMap<>();
                Map<Integer, LinkedList<String>> userMailsByPriorityOrderMap2 = new LinkedHashMap<>();
                if(!Utils.isNullOrEmpty(userMails1)){
                    emailSenderMap.forEach((priorityOrderLevel, emailTokenMap) -> {
                        LinkedList<String> senderMaillist = new LinkedList<>(emailTokenMap.keySet().stream().filter(userMails1::contains).toList());
                        if(!Utils.isNullOrEmpty(senderMaillist))
                            userMailsByPriorityOrderMap1.put(priorityOrderLevel, senderMaillist);
                    });
                }
                if(!Utils.isNullOrEmpty(userMails2)){
                    emailSenderMap.forEach((priorityOrderLevel, emailTokenMap) -> {
                        LinkedList<String> senderMaillist = new LinkedList<>(emailTokenMap.keySet().stream().filter(userMails2::contains).toList());
                        if(!Utils.isNullOrEmpty(senderMaillist))
                            userMailsByPriorityOrderMap2.put(priorityOrderLevel, senderMaillist);
                    });
                }
                Constants.USER_MAILS_1 = userMailsByPriorityOrderMap1;
                Constants.USER_MAILS_2 = userMailsByPriorityOrderMap2;
            }
        }
    }

    @PostConstruct
    public void setEmailTokenData() {
        List<ErpNotificationEmailSenderSettingsDBO> erpNotificationEmailSenderSettingsDBOList = commonApiTransaction.getEmailSenderSettings();
        if(!Utils.isNullOrEmpty(erpNotificationEmailSenderSettingsDBOList)) {
            Constants.PRIORITY_MAILS_TOKEN = erpNotificationEmailSenderSettingsDBOList.stream()
                .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,
                Collectors.toMap(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, ErpNotificationEmailSenderSettingsDBO::getToken)));
            Constants.PRIORITY_MAILS = erpNotificationEmailSenderSettingsDBOList.stream()
                .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,
                Collectors.mapping(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, Collectors.toCollection(LinkedList::new))));
//            Constants.PRIORITY_MAILS_STATUS = erpNotificationEmailSenderSettingsDBOList.stream()
//                    .collect(Collectors.groupingBy(ErpNotificationEmailSenderSettingsDBO::getPriorityLevelOrder,
                            //Collectors.mapping(Tuples.of( getSenderEmail, "")));
                            //Collectors.mapping(ErpNotificationEmailSenderSettingsDBO::getSenderEmail, Collectors.toCollection(LinkedList::new))));
            Constants.PRIORITY_MAILS_STATUS = erpNotificationEmailSenderSettingsDBOList.stream()
                    .collect(HashMap::new,
                        (map, erpNotificationEmailSenderSettingsDBO) -> {
//                            LinkedList<Tuple4<String, String, Boolean, LocalDateTime>> tupleList = map.computeIfAbsent(erpNotificationEmailSenderSettingsDBO.getPriorityLevelOrder(), k -> new LinkedList<>());
//                            tupleList.add(Tuples.of(erpNotificationEmailSenderSettingsDBO.getSenderEmail(), "", false, null));
                            LinkedList<Tuple4<String, String, Boolean, LocalDateTime>> tupleList = map.computeIfAbsent(erpNotificationEmailSenderSettingsDBO.getPriorityLevelOrder(), k -> new LinkedList<>());
                            tupleList.add(Tuples.of(erpNotificationEmailSenderSettingsDBO.getSenderEmail(), "", false, LocalDateTime.now()));
                        },
                        HashMap::putAll);
        }
    }
}
