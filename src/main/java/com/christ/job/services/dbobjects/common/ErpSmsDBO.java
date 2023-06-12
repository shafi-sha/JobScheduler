package com.christ.job.services.dbobjects.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_sms")

@Setter
@Getter
public class ErpSmsDBO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_sms_id")
	public int id;
	
	@Column(name="erp_work_flow_process_notifications_id")
	public Integer erpWorkFlowProcessNotificationsDBO;
	
	@Column(name="erp_entries_id")
	public Integer entryId;

	@Column(name="erp_sms_user_entries_id")
	public Integer erpSmsUserEntriesDbo;
	
	@Column(name="erp_users_id")
	public Integer erpUsersDBO;
	
	@Column(name="student_id")
	public Integer studentId;

	@Column(name="sender_mobile_no")
	public String senderMobileNo;

	@Column(name="recipient_mobile_no")
	public String recipientMobileNo;
	
	@Column(name="sms_content",columnDefinition="TEXT")
	public String smsContent;
	
	@Column(name="sms_is_sent")
	public boolean smsIsSent;
	
	@Column(name="sms_sent_time")
	public LocalDateTime smsSentTime;
	
	@Column(name="sms_is_delivered")
	public boolean smsIsDelivered;
	
	@Column(name="sms_delivered_time")
	public LocalDateTime smsDeliveredTime;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
	
	@Column(name = "erp_reminder_notifications_id")
	private Integer erpReminderNotificationsDBO;
}
