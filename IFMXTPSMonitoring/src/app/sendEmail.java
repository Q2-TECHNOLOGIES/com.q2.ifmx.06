/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.Authenticator
 *  javax.mail.Message
 *  javax.mail.Message$RecipientType
 *  javax.mail.MessagingException
 *  javax.mail.PasswordAuthentication
 *  javax.mail.Session
 *  javax.mail.Transport
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package app;

import Common.Logging;
import Common.PropertiesLoader;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendEmail {
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("app");
    public static PropertiesLoader pl;

    public static void sendEmail(String currentDate, String currentTime, String tpsType, String serverIP, HashMap<String, Integer> values, int maxTPS, String sender, String passwordSender, String[] recipients, String hostname, String port, String auth, String starttls) {
        Properties prop = new Properties();
        Logging log = new Logging(); 
        log.configLog(pl,logger,loggerContext);
        
        System.out.println("Sender Email : " + sender);
        final String username = pl.sender;
        final String password = pl.passwordSender;
        prop.put("mail.smtp.host", pl.hostname);
        prop.put("mail.smtp.port", pl.port);
        prop.put("mail.smtp.auth", pl.auth);
        prop.put("mail.smtp.ssl.trust", "*");
        for (String recipient : recipients) {
            System.out.println("processing sending message to " + recipient + "...");
            logger.info("processing sending message to " + recipient + "...");
            Session session = Session.getInstance((Properties)prop, (Authenticator)new Authenticator(){

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom((Address)new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO, (Address[])InternetAddress.parse((String)recipient));
                String text1 = "Dear User,\n\nSystem founds issue on log process with details below:\n       Batch Process Date : " + currentDate + "\n       Batch Process Time : " + currentTime + "\n       Message :  maximum " + tpsType + " has been reached\n       Max TPS Allowed :  " + maxTPS;
                String text2 = "";
                for (Map.Entry<String, Integer> pair : values.entrySet()) {
                    text2 = text2 + "\n       Current TPS on server " + pair.getKey() + " : " + pair.getValue();
                }
                String text = text1 + text2;
                message.setSubject("TPS LOG PROCESS");
                message.setText(text);
                Transport.send((Message)message);
                System.out.println("Email sent!");
                logger.info("Email sent to " + recipient);
            }
            catch (MessagingException e) {
                e.printStackTrace();
                logger.info("System get error" + e.getMessage());
            }
        }
    }
}
