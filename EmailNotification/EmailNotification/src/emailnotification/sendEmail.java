/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailnotification;

import static emailnotification.app.logger;
import java.util.HashMap;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.net.ssl.X509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author rizaac
 */
public class sendEmail {
    final static Logger logger = LogManager.getLogger(app.class);
    
    public static String trxMapped(String trxMapping){
        HashMap<String, String> mapColumn  = new HashMap<String, String>() {{
            put("SESSION", "A Session");
            put("ENROLLMENT", "An Enrollment");             
            put("INTERNAL", "An IT");               
            put("DOMESTIC", "An Domestic");               
            put("P2P", "An P2P");                    
            put("WIRE", "An Wire");                   
            put("ADDRESS_BASED_PAYMENT", "An Address Based Payment");  
            put("INFO_CHANGES", "An Service");           
        }};

        String values ="";
        for (String column : mapColumn.keySet()) {
            if(trxMapping.equals(column)){
                values =  mapColumn.get(column);
            }
        }
        return values;
    }
   

    public static boolean sendEmail(String messageTemplate, String trxMapping, String ruleName, String sender, String passwordSender, 
            String [] recipients, String hostname, String port, String auth, String starttls,
            Connection conn, String trxKey) throws SQLException{
        boolean status = false;
        
            
        System.out.println("Sender Email : " + sender);
        logger.info("Sender Email : " + sender);
        final String username = sender;
        final String password = passwordSender;
        
        Properties prop = new Properties();
        prop.put("mail.smtp.host", hostname);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", auth);
        prop.put("mail.smtp.ssl.trust", "*");
        //prop.put("mail.smtp.starttls.enable", true); //TLS
        
        for (String recipient : recipients) {
            System.out.println("processing sending message to " + recipient + "...");
            logger.info("processing sending message to " + recipient + "...");
            Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(recipient)
                );

                String text = (messageTemplate);

                message.setSubject(trxMapped(trxMapping) + " Policy Rule is triggered - Rule Name : " + ruleName);
                message.setContent(text,"text/html");

                Transport.send(message);

                System.out.println("Email sent to " + recipient);
                logger.info("Email sent to " + recipient);
                status = true;
            //} catch (MessagingException e) {
            } catch (Exception e) {
                status = false;
                //e.printStackTrace();
                System.out.println("Processs sending email get error " + e.getMessage());
                logger.error("Processs sending email get error " + e.getMessage());
            }
            
        }
        return status;

    }
    
}
