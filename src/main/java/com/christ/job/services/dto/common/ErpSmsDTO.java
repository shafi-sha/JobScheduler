package com.christ.job.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpSmsDTO {

    private int id;
    private String recipientMobileNo;
    private String smsContent;
    private String smsIsSent;
    private String senderMobileNo;
    private String smsSentTime;
    private String smsIsDelivered;
    private String smsDeliveredTime;
    private Integer messageStatus;

}
