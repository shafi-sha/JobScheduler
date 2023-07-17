package com.christ.job.services.common;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;

import java.time.LocalDateTime;
import java.util.*;

public class Constants {

    public Constants() {
    }

    public static int SEND_MAIL_COUNT = 0;
    public static int SEND_SMS_COUNT = 0;
    public static final String SMS_SEND_URL = "https://api-alerts.kaleyra.com/v4/?api_key=752963615094k9zz8gk1&method=sms&entity_id=1101356700000026901&sender=SMSCHR&";
    public static final String SMS_SEND_STATUS_URL = "https://api-alerts.kaleyra.com/v4/?api_key=752963615094k9zz8gk1&method=sms.status&";
    public static final String EMAIL_REFRESH_TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    public static int SEND_SMS_CHECK_COUNT = 0;
    public static int LAST_MAIL_SEND_COUNT= 0;
    public static Map<Integer, LinkedList<String>> USER_MAILS_1 = new LinkedHashMap<>();
    public static Map<Integer, LinkedList<String>> USER_MAILS_2 = new LinkedHashMap<>();
    public static Map<Integer, LinkedList<Tuple4<String, String, Boolean, LocalDateTime>>> PRIORITY_MAILS_STATUS = new LinkedHashMap<>(); //priority level, { emailId, exceptionName, emailSentFlag, exceptionOccuredDate }
    public static Map<Integer, LinkedList<String>> PRIORITY_MAILS = new LinkedHashMap<>();
    public static Map<Integer, Map<String, String>> PRIORITY_MAILS_TOKEN = new LinkedHashMap<>();
    public static String MAIL_USERNAME = "";
    public static Map<Integer, String> PRIORITY_LAST_USED_MAIL = new LinkedHashMap<>();
    public static Map<String, String> PRIORITY_FAILED_MAILS = new LinkedHashMap<>();

}
