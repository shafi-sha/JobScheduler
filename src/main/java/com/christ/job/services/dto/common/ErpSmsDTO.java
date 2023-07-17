package com.christ.job.services.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErpSmsDTO {

    private int id;
    private String senderMobileNo;
    private String recipientMobileNo;
    private String smsSubject;
    private String smsContent;
    private Boolean smsIsSent;
    private LocalDateTime smsSentTime;
    private Boolean smsIsDelivered;
    private LocalDateTime smsDeliveredTime;
    private String messageStatus;
    private String templateId;
    private String gatewayResponse;
    private String smsTransactionId;

}
