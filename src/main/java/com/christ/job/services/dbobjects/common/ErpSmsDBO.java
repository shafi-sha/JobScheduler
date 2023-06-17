package com.christ.job.services.dbobjects.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_sms")
@Setter
@Getter
public class ErpSmsDBO implements Serializable{

	private static final long serialVersionUID = 8841255585680757282L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_sms_id")
	private int id;
	
	@Column(name="erp_work_flow_process_notifications_id")
	private Integer erpWorkFlowProcessNotificationsDBO;

	@Column(name="erp_entries_id")
	private Integer entryId;

	@Column(name="erp_sms_user_entries_id")
	private Integer erpSmsUserEntriesDbo;

	@Column(name="erp_users_id")
	private Integer erpUsersDBO;

	@Column(name="student_id")
	private Integer studentId;

	@Column(name="sender_mobile_no")
	private String senderMobileNo;

	@Column(name="recipient_mobile_no")
	private String recipientMobileNo;

	@Column(name="sms_subject")
	private String smsSubject;
	
	@Column(name="sms_content",columnDefinition="TEXT")
	private String smsContent;
	
	@Column(name="sms_is_sent")
	private Boolean smsIsSent;

	@Column(name="sms_sent_time")
	private LocalDateTime smsSentTime;

	@Column(name="sms_is_delivered")
	private Boolean smsIsDelivered;
	
	@Column(name="sms_delivered_time")
	private LocalDateTime smsDeliveredTime;

	@Column(name = "erp_reminder_notifications_id")
	private Integer erpReminderNotificationsDBO;

	@Column(name="template_id")
	private String templateId;

	@Column(name="gateway_response",columnDefinition="TEXT")
	private String gatewayResponse;

	@Column(name="message_status")
	private String messageStatus;

	@Column(name="created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name="modified_users_id")
	private Integer modifiedUsersId;	
	
	@Column(name="record_status")
	private char recordStatus;
	
}
