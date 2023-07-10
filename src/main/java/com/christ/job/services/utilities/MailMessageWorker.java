package com.christ.job.services.utilities;

import com.christ.job.services.common.Constants;
import com.christ.job.services.common.RedisSysPropertiesData;
import com.christ.job.services.common.SysProperties;
import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpEmailsDBO;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
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
            //priorityMailsMap.get(priorityOrderLevel).contains(lastUsedPriorityMail)
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
            System.out.println("mail : "+mail);
            Constants.PRIORITY_LAST_USED_MAIL.put(priorityOrderLevel, mail);
            return mail;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void sendIntimationToERP(String emailContent, String emailSubject, String senderName, Integer priorityLevelOrder, String userName, String exceptionName){
        ErpEmailsDBO emailsDBO = new ErpEmailsDBO();
        emailsDBO.setSenderName(senderName);
        emailsDBO.setEmailContent(emailContent);
        emailsDBO.setEmailSubject(emailSubject);
        emailsDBO.setPriorityLevelOrder(priorityLevelOrder);
        emailsDBO.setEmailIsSent(false);
        emailsDBO.setRecipientEmail(redisSysPropertiesData.getSysProperties(SysProperties.ERP_EMAIL.name(), null, null));
        emailsDBO.setRecordStatus('A');
        saveErpEmailsDBO(emailsDBO);

        Constants.PRIORITY_MAILS_STATUS.get(priorityOrderLevel).forEach(tuple -> {
            if(tuple.getT1().equalsIgnoreCase(userName)){
                tuple = Tuples.of(tuple.getT1(), exceptionName, true, LocalDateTime.now());
            }
        });
    }

    public void saveErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        this.sessionFactory.withTransaction((s, tx) -> s.persist(erpEmailsDBO)).subscribeAsCompletionStage();
    }

    public void updateErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        this.sessionFactory.withTransaction((s, tx) -> s.merge(erpEmailsDBO)).subscribeAsCompletionStage();
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
                    sendIntimationToERP(emailContent, emailSubject, senderName, this.priorityOrderLevel, this.userName, "daily quota exceeded");
                } else if(m.toString().contains("334") || m.toString().contains("jakarta.mail.MessagingException: 334")){
                    System.out.println("Token expired");
                    //send mail to erp support- recepient mail should be sys_properties
                    String emailSubject = "Token Expired";
                    String senderName = "Christ University";
                    String emailContent = "Token has been expired for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, this.priorityOrderLevel, this.userName, "token expired");
                } else {
                    System.out.println("Email Sending Failed");
                    String emailSubject = "Email Sending Failed";
                    String senderName = "Christ University";
                    String emailContent = "Something went wrong for email: "+this.userName;
                    sendIntimationToERP(emailContent, emailSubject, senderName, this.priorityOrderLevel, this.userName, "something went wrong");
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
