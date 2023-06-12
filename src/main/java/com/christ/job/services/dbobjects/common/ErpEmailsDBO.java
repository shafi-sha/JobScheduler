package com.christ.job.services.dbobjects.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_emails")
@Setter
@Getter
public class ErpEmailsDBO implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_emails_id")
	public int id;
	
	@Column(name="erp_work_flow_process_notifications_id")
	public Integer erpWorkFlowProcessNotificationsDBO;
	
	@Column(name="erp_entries_id")
	public Integer entryId;
	
	@Column(name="erp_emails_user_entries_id")
	public Integer erpEmailsUserEntriesDBO;
	
	@Column(name="erp_users_id")
	public Integer erpUsersDBO;

	@Column(name = "erp_reminder_notifications_id")
	private Integer erpReminderNotificationsDBO;
	
	@Column(name="erp_student_id")
	public Integer studentId;

	@Column(name="recipient_email")
	public String recipientEmail;
	
	@Column(name="email_content",columnDefinition="TEXT")
	public String emailContent;
	
	@Column(name="email_is_sent")
	public boolean emailIsSent;
	
	@Column(name="email_sent_time")
	public LocalDateTime emailSentTime;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
}
