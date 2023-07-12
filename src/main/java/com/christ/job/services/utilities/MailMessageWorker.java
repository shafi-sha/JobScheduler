package com.christ.job.services.utilities;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpEmailsDBO;
import com.christ.utility.lib.caching.CacheUtils;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jdk.jshell.execution.Util;
import org.hibernate.reactive.mutiny.Mutiny;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class MailMessageWorker implements Runnable{

    private String userName;
    private String token;
    private ErpEmailsDBO erpEmailsDBO;
    private Integer priorityOrderLevel;
    private Mutiny.SessionFactory sessionFactory;
    private RedisSysPropertiesData redisSysPropertiesData;

    public MailMessageWorker(String userName, String token, Integer priorityOrderLevel, ErpEmailsDBO erpEmailsDBO, Mutiny.SessionFactory sessionFactory, RedisSysPropertiesData redisSysPropertiesData) {
        this.userName = userName;
        this.token = token;
        this.priorityOrderLevel = priorityOrderLevel;
        this.erpEmailsDBO = erpEmailsDBO;
        this.sessionFactory = sessionFactory;
        this.redisSysPropertiesData = redisSysPropertiesData;
    }

    public static synchronized String getUserNameByPriorityOrderForEmail(int count, Integer priorityOrderLevel){
        try{
            Tuple4<String, String, Boolean, LocalDateTime> tuple = Constants.PRIORITY_MAILS_STATUS.get(priorityOrderLevel).get(count);
            if(Utils.isNullOrEmpty(tuple.getT2())){//checking if exception is occured before for this specific email id
                return tuple.getT1();
            } else {
                if(!tuple.getT3()){//boolean for indicating mail send to erpsupport regarding the exception occured for this specific email id
                    //send intimation email to erp support
                } else {
                    Duration duration = Duration.between(tuple.getT4(), LocalDateTime.now());
                    if(duration.toDays() >= 1) {
                        Tuple4<String, String, Boolean, LocalDateTime> modifiedTuple = Tuples.of(tuple.getT1(), "", false, LocalDateTime.now());
                        Constants.PRIORITY_MAILS_STATUS.get(priorityOrderLevel).set(count, modifiedTuple);
                    }
                }
                count += 1;
                return Constants.PRIORITY_MAILS_STATUS.get(priorityOrderLevel).get(count).getT1(); //next email id
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String getUserNameByPriorityOrderForEmailNew(Integer priorityOrderLevel,
        Map<Integer, LinkedList<String>> priorityMailsMap){
        try{
            String lastUsedPriorityMail = "";
            int index = 0;
            System.out.println("index before : "+index);
            if(Constants.PRIORITY_LAST_USED_MAIL.containsKey(priorityOrderLevel)){
                lastUsedPriorityMail = Constants.PRIORITY_LAST_USED_MAIL.get(priorityOrderLevel);
                System.out.println("lastUsedPriorityMail : "+lastUsedPriorityMail);
                index = priorityMailsMap.get(priorityOrderLevel).indexOf(lastUsedPriorityMail);
                if (index == (priorityMailsMap.get(priorityOrderLevel).size() - 1)) {
                    index = 0;
                } else {
                    index++;
                }
            }
            System.out.println("index after : "+index);
            String mail = priorityMailsMap.get(priorityOrderLevel).get(index);
            System.out.println("mail check before : "+mail);
            Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
            //mail = getUserNameByPriorityOrderForEmailCheck1(priorityOrderLevel, priorityMailsMap, mail, index);
            mail = getUserNameByPriorityOrderForEmailCheckWithRedis(priorityOrderLevel, priorityMailsMap.get(priorityOrderLevel), mail, index);
            System.out.println("mail check after : "+mail);
            return mail;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String getUserNameByPriorityOrderForEmailNew1(Integer priorityOrderLevel,
        Map<Integer, LinkedList<String>> priorityMailsMap){
        try{
            String lastUsedPriorityMail = CacheUtils.instance.get("__priority_last_used_emails_", String.valueOf(priorityOrderLevel));
            int index = 0;
            System.out.println("index before : "+index);
            System.out.println("lastUsedPriorityMail : "+lastUsedPriorityMail);
            LinkedList<String> priorityMails = priorityMailsMap.get(priorityOrderLevel);
            System.out.println("priorityMails : "+priorityMails);
            if(!Utils.isNullOrEmpty(lastUsedPriorityMail)){
                index = priorityMails.indexOf(lastUsedPriorityMail);
                System.out.println("index of "+lastUsedPriorityMail+" : "+ index);
                if (index == (priorityMails.size() - 1)) {
                    index = 0;
                } else {
                    index++;
                }
            }
            System.out.println("index after : "+index);
            String mail = priorityMails.get(index);
            System.out.println("mail check before : "+mail);
            //Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
            //CacheUtils.instance.set("__priority_last_used_emails_", String.valueOf(priorityOrderLevel), mail);
            //mail = getUserNameByPriorityOrderForEmailCheck1(priorityOrderLevel, priorityMailsMap, mail, index);
            mail = getUserNameByPriorityOrderForEmailCheckWithRedis(priorityOrderLevel, priorityMails, mail, index);
            System.out.println("mail check after : "+mail);
            return mail;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String getUserNameByPriorityOrderForEmailCheckWithRedis(Integer priorityOrderLevel,
                                                                                       LinkedList<String> priorityMails, String email, int index){
        String mail = email;
        try{
            if(!Utils.isNullOrEmpty(priorityMails)){
                int allMailsExceptionCount = 0;
                for (int i = index; i < priorityMails.size(); i++) {
                    String emailExceptionData = CacheUtils.instance.get("__priority_failed_emails_map_", priorityMails.get(i));
                    System.out.println("emailExceptionData of i="+i+" "+ priorityMails.get(i) + " : " +emailExceptionData);
                    System.out.println("allMailsExceptionCount : "+ allMailsExceptionCount);
                    if(Utils.isNullOrEmpty(emailExceptionData)){
//                        if (i == (priorityMails.size() - 1)) {
//                            mail = priorityMails.get(0);
//                            System.out.println("0th mail "+ mail);
//                            break;
//                        } else {
//                            mail = priorityMails.get(i);
//                            System.out.println("next mail "+ mail);
//                            break;
//                        }
                        mail = priorityMails.get(i);
                        System.out.println("next mail "+ mail);
                        break;
                    } else {
                        System.out.println("mail "+priorityMails.get(i)+" got exception : ");
                        long day = Duration.between(LocalDateTime.now(), Utils.convertStringLocalDateTimeToLocalDateTime1(emailExceptionData.split("_")[1])).toDays();
                        System.out.println("day : "+day);
                        if(day >= 1){
                            CacheUtils.instance.clearKey("__priority_failed_emails_map_", priorityMails.get(i));
                            System.out.println("mail " +priorityMails.get(i)+ " exception cleared");
                        }
                        allMailsExceptionCount++;
                        if (i == (priorityMails.size() - 1)) {
                            System.out.println("made i=0");
                            i = -1;
                        }
                        if(allMailsExceptionCount == priorityMails.size()){
                            System.out.println("all priority mails got exception");
                            break;
                        }
                    }
                }
            }
            CacheUtils.instance.set("__priority_last_used_emails_", String.valueOf(priorityOrderLevel), mail);
            System.out.println("mail after checking exception : "+mail);
        }catch(Exception e){
            e.printStackTrace();
        }
        return mail;
    }

    public static synchronized String getUserNameByPriorityOrderForEmailCheck(Integer priorityOrderLevel,
        Map<Integer, LinkedList<String>> priorityMailsMap){
        try{
//            String lastUsedPriorityMail = "";
//            int index = 0;
//            if(Constants.PRIORITY_LAST_USED_MAIL.containsKey(priorityOrderLevel)){
//                lastUsedPriorityMail = Constants.PRIORITY_LAST_USED_MAIL.get(priorityOrderLevel);
//                index = priorityMailsMap.get(priorityOrderLevel).indexOf(lastUsedPriorityMail);
//                if (index == (priorityMailsMap.get(priorityOrderLevel).size() - 1)) {
//                    index = 0;
//                } else {
//                    index++;
//                }
//            }
//            String mail = priorityMailsMap.get(priorityOrderLevel).get(index);
//            Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
            String mail = "";
            if(!Utils.isNullOrEmpty(Constants.PRIORITY_FAILED_MAILS)){
                if(!Utils.isNullOrEmpty(priorityMailsMap.get(priorityOrderLevel))){
                    LinkedList<String> mailList = priorityMailsMap.get(priorityOrderLevel);
                    for (int i = 0; i < mailList.size(); i++) {
                        if(!Constants.PRIORITY_FAILED_MAILS.containsKey(mailList.get(i))){
                            if(Constants.PRIORITY_LAST_USED_MAIL.containsKey(priorityOrderLevel)){
                                if(mailList.get(i).equalsIgnoreCase(Constants.PRIORITY_LAST_USED_MAIL.get(priorityOrderLevel))){
                                    if (i == (priorityMailsMap.get(priorityOrderLevel).size() - 1)) {
                                        mail = mailList.get(i);
                                    } else {
                                        mail = mailList.get(i+1);
                                    }
                                    Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
                                }
                            } else {
                                mail = mailList.get(i);
                                Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
                            }
                        } else {
                            String[] mailExceptionData = Constants.PRIORITY_FAILED_MAILS.get(mailList.get(i)).split("_");
                            //if("quotaExceeded".equalsIgnoreCase(exceptionData[0]))
                            long day = Duration.between(LocalDateTime.now(), Utils.convertStringLocalDateTimeToLocalDateTime(mailExceptionData[1])).toDays();
                            if(day > 1){
                                Constants.PRIORITY_FAILED_MAILS.remove(mailList.get(i));
                            }
                        }
                    }
                }
            } else {
                if(!Utils.isNullOrEmpty(priorityMailsMap.get(priorityOrderLevel))){
                    LinkedList<String> mailList = priorityMailsMap.get(priorityOrderLevel);
                    for (int i = 0; i < mailList.size(); i++) {
                        if(Constants.PRIORITY_LAST_USED_MAIL.containsKey(priorityOrderLevel)){
                            if(mailList.get(i).equalsIgnoreCase(Constants.PRIORITY_LAST_USED_MAIL.get(priorityOrderLevel))){
                                if (i == (priorityMailsMap.get(priorityOrderLevel).size() - 1)) {
                                    mail = mailList.get(i);
                                } else {
                                    mail = mailList.get(i+1);
                                }
                                Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
                            }
                        } else {
                            mail = mailList.get(i);
                            Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
                        }
                    }
                }
            }
            System.out.println("mail : "+mail);
            return Utils.isNullOrEmpty(mail) ? priorityMailsMap.get(priorityOrderLevel).get(0) : mail;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String getUserNameByPriorityOrderForEmailCheck1(Integer priorityOrderLevel,
        Map<Integer, LinkedList<String>> priorityMailsMap, String email, int index){
        try{
            String mail = email;
            if(!Utils.isNullOrEmpty(Constants.PRIORITY_FAILED_MAILS)){
                if(!Utils.isNullOrEmpty(priorityMailsMap.get(priorityOrderLevel))){
                    LinkedList<String> mailList = priorityMailsMap.get(priorityOrderLevel);
                    for (int i = index; i < mailList.size(); i++) {
                        if(!Constants.PRIORITY_FAILED_MAILS.containsKey(mailList.get(i))){
                            if (i == (priorityMailsMap.get(priorityOrderLevel).size() - 1)) {
                                mail = mailList.get(0);
                            } else {
                                mail = mailList.get(i+1);
                            }
                            Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
                        } else {
                            String[] mailExceptionData = Constants.PRIORITY_FAILED_MAILS.get(mailList.get(i)).split("_");
                            //if("quotaExceeded".equalsIgnoreCase(exceptionData[0]))
                            long day = Duration.between(LocalDateTime.now(), Utils.convertStringLocalDateTimeToLocalDateTime(mailExceptionData[1])).toDays();
                            if(day > 1){
                                Constants.PRIORITY_FAILED_MAILS.remove(mailList.get(i));
                            }
                        }
                    }
                }
            }
            System.out.println("mail : "+mail);
            return mail;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void sendIntimationToERP(String emailContent, String emailSubject, String senderName, Integer priorityLevelOrder, String userName, String exceptionName){
        //check already send email
        boolean isEmailSendRequired = true;
        String emailExceptionData = CacheUtils.instance.get("__priority_failed_emails_map_", userName);
        System.out.println("sendIntimationToERP emailExceptionData");
        if(!Utils.isNullOrEmpty(emailExceptionData)){
            long day = Duration.between(LocalDateTime.now(), Utils.convertStringLocalDateTimeToLocalDateTime1(emailExceptionData.split("_")[1])).toDays();
            System.out.println("sendIntimationToERP day : "+day);
            if(day < 1){
                isEmailSendRequired = false;
            }
        } else {
            System.out.println("sendIntimationToERP emailExceptionData");
            CacheUtils.instance.set("__priority_failed_emails_map_", userName, exceptionName +"_"+ Utils.convertLocalDateTimeToStringDateTime1(LocalDateTime.now()));
        }
        if(isEmailSendRequired){
            System.out.println("sendIntimationToERP email send");
            ErpEmailsDBO emailsDBO = new ErpEmailsDBO();
            emailsDBO.setSenderName(senderName);
            emailsDBO.setEmailContent(emailContent);
            emailsDBO.setEmailSubject(emailSubject);
            emailsDBO.setPriorityLevelOrder(priorityLevelOrder);
            emailsDBO.setEmailIsSent(false);
            emailsDBO.setRecipientEmail(redisSysPropertiesData.getSysProperties(SysProperties.ERP_INTIMATION_EMAIL.name(), null, null));
            emailsDBO.setRecordStatus('A');
            saveErpEmailsDBO(emailsDBO);
        }

//        Constants.PRIORITY_MAILS_STATUS.get(priorityOrderLevel).forEach(tuple -> {
//            if(tuple.getT1().equalsIgnoreCase(userName)){
//                tuple = Tuples.of(tuple.getT1(), exceptionName, true, LocalDateTime.now());
//            }
//        });
//        Map<String, String> exceptionMailMap = new LinkedHashMap<>();
//        exceptionMailMap.put(exceptionName, LocalDateTime.now().toString());
        //Utils.convertStringLocalDateTimeToLocalDateTime()

        //Constants.PRIORITY_FAILED_MAILS.put(userName, exceptionName +"_"+ Utils.convertLocalDateTimeToStringDateTime(LocalDateTime.now()));

    }

    public void saveErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        this.sessionFactory.withTransaction((s, tx) -> s.persist(erpEmailsDBO)).subscribeAsCompletionStage();
        //this.sessionFactory.close();
    }

    public void updateErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        this.sessionFactory.withTransaction((s, tx) -> s.merge(erpEmailsDBO)).subscribeAsCompletionStage();
        //this.sessionFactory.close();
    }

    @Override
    public void run() {
        if(!Utils.isNullOrEmpty(userName) && !Utils.isNullOrEmpty(erpEmailsDBO)){
            SMTPTransport transport = null;
            Session session;
            try{
                Properties props = new Properties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.starttls.enable", "true");
                session = Session.getDefaultInstance(props);
                if(!Utils.isNullOrEmpty(session)){
                    String recipientEmail = erpEmailsDBO.getRecipientEmail();
                    if(Utils.isValidEmail(recipientEmail)){
                        MimeMessage message = new MimeMessage(session);
                        InternetAddress from = new InternetAddress(userName, !Utils.isNullOrEmpty(erpEmailsDBO.getSenderName()) ? erpEmailsDBO.getSenderName() : "Christ University");
                        InternetAddress to = new InternetAddress(recipientEmail);
                        message.setFrom(from);
                        message.addRecipient(Message.RecipientType.TO, to);
                        message.setSubject(erpEmailsDBO.getEmailSubject());
                        MimeMultipart mimeMultipart = new MimeMultipart();
                        MimeBodyPart mimeBodyPart = new MimeBodyPart();
                        mimeBodyPart.setContent(erpEmailsDBO.getEmailContent(), "text/html");
                        mimeMultipart.addBodyPart(mimeBodyPart);
                        message.setContent(mimeMultipart);
                        transport = new SMTPTransport(session, null);
                        transport.connect("smtp.gmail.com", this.userName, null);
                        transport.issueCommand("AUTH XOAUTH2 " + new String(BASE64EncoderStream.encode(String.format("user=%s\1auth=Bearer %s\1\1", this.userName, this.token ).getBytes())), 235);
                        if(transport.isConnected()){
                            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
                        }
                        transport.close();
                        erpEmailsDBO.setEmailIsSent(true);
                        erpEmailsDBO.setEmailSentTime(LocalDateTime.now());
                        updateErpEmailsDBO(erpEmailsDBO);
                    }
                }
            }catch (MessagingException m) {
                m.printStackTrace();
                if (transport != null) {
                    try {
                        transport.close();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
				System.out.println("--------------------------------------------------------------------------------------------------------");
				System.out.println("Excetption occurs while sending mail frm '" + userName + "' by "+ Thread.currentThread().getName());
				System.out.println(m.toString());
                if(m.toString().contains("Daily user sending quota exceeded") || m.toString().contains("550 5.4.5")){
                    System.out.println("Email Changed");
                    //MailMessageWorker.getUserNameByPriorityOrderForEmail(userName, 0, priorityOrderLevel);
                    String emailSubject = "Daily user sending quota exceeded";
                    String senderName = "Christ University";
                    String emailContent = "Daily user sending quota exceeded for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, this.priorityOrderLevel, this.userName, "quotaExceeded");
                } else if(m.toString().contains("334") || m.toString().contains("jakarta.mail.MessagingException: 334")){
                    System.out.println("Token expired");
                    //send mail to erp support- recepient mail should be sys_properties
                    String emailSubject = "Token Expired";
                    String senderName = "Christ University";
                    String emailContent = "Token has been expired for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, this.priorityOrderLevel, this.userName, "tokenExpired");
                } else {
                    System.out.println("Email Sending Failed");
                    String emailSubject = "Email Sending Failed";
                    String senderName = "Christ University";
                    String emailContent = "Something went wrong for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, this.priorityOrderLevel, this.userName, "wentwrong");
                }
                System.out.println("--------------------------------------------------------------------------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (transport != null && transport.isConnected()) {
                    try {
                        transport.close();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
