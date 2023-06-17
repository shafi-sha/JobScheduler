package com.christ.job.services.dbobjects.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "erp_notification_email_sender_settings")
@Setter
@Getter
public class ErpNotificationEmailSenderSettingsDBO implements Serializable {

    private static final long serialVersionUID = 3658307026873613967L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_notification_email_sender_settings_id")
    private int id;

    @Column(name="sender_email")
    private String senderEmail;

    @Column(name="client_id")
    private String clientId;

    @Column(name="client_secret")
    private String clientSecret;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="token")
    private String token;

    @Column(name="priority_level_order")
    private Integer priorityLevelOrder;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
