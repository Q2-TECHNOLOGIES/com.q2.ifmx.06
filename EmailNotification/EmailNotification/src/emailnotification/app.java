/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailnotification;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Decryptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static emailnotification.sendEmail.sendEmail;
import static emailnotification.mappingColumnTemplate.mappingColumn;
import static emailnotification.sendStatus.updateSendStatus;
import static emailnotification.sendStatus.updateSendStatusMessageNull;
import static emailnotification.sendStatus.updateSendStatusMessageError;
import static emailnotification.messageContent.getMessageTemplate;
import static emailnotification.messageContent.getVariableTemplate;
import static emailnotification.messageContent.mappingValues;
import static emailnotification.messageContent.messageText;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.isNumeric;
/**
 *
 * @author rizaac
 */
public class app {

    final static Logger logger = LogManager.getLogger(app.class);
    
    public static String senderEmail(String senderMB, String senderIB, String unknownSender, String channelType){
        if(channelType.equalsIgnoreCase("OCTO Mobile")){
            return senderMB;
        }else if(channelType.equalsIgnoreCase("OCTO Clicks")){
            return senderIB;
        }else{
            return unknownSender;
        }
    }
    
    public static String passSenderEmail(String passwordMB, String passwordIB, String passwordUS, String channelType){
        if(channelType.equalsIgnoreCase("OCTO Mobile")){
            return passwordMB;
        }else if(channelType.equalsIgnoreCase("OCTO Clicks")){
            return passwordIB;
        }else{
            return passwordUS;
        }
    }
    
    public static String decryptedPass(String password) throws Exception{
        Decryptor dec = new Decryptor();
        if(password.isEmpty() | password.equals("null")){
            return "";
        }else{
            return dec.decrypt(password);
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        //System.out.println(args[0] + args[1] + args[2] + args[3] + args[4]);
//        String db_host = args[0];
        String db_host = null;
        String db_port = args[1];
        String db_username = args[2]; 
        String db_name = args[3]; 
        String db_password = args[4];
        String senderMB = args[5];
        String passwordMB = args[6];                                            
        String senderIB = args[7];
        String passwordIB = args[8];
        String unknownSender = args[9];
        String passwordUS = args[10];
        String [] recipients = args[11].split(";");
        String hostname = args[12];
        String port = args[13];
        String auth = args[14];
        String starttls = args[15];
        
        
        Connection conn = null ;
        String line;
        String path = System.getenv("MAIN_PATH");
        String cmd = path+"/emailnotification_config.bat";
        Runtime runtime = Runtime.getRuntime();
        do 
        {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                db_host = line;
            }
            conn = dbConn.getdbconn(db_host, db_port, db_username, db_name, decryptedPass(db_password));
             if (conn == null) {
                 System.out.println("Pengecekan ulang koneksi");
                 logger.info("Koneksi Gagal, Pengecekan ulang koneksi ");
             }
        }while(conn == null);
        System.out.println("Koneksi Berhasil, Lanjut Send Email");
        try {
            
            ResultSet rs;
            PreparedStatement preparedStatement = null;
            String strQuery = "select  * from IDB.dbo.V_IMPL_EMAIL_NOTIFICATION WHERE SEND_STATUS = 0" ; 
            preparedStatement = conn.prepareStatement(strQuery);
            rs = preparedStatement.executeQuery();
            
            while(rs.next()) {
                String trxMapping = rs.getString("TRX_MAPPING");
                String channelTypeDesc = rs.getString("CHANNEL_TYPE_DESC");
                String notifTemplateCode = rs.getString("NOTIF_TEMPLATE_CODE");
                String trxKey = rs.getString("TRX_KEY");
                String actTrxKey = rs.getString("ACT_TRX_KEY");
                String allRuleName =  rs.getString("ALL_RULE_NAME");
                String trxType = rs.getString("TRX_TYPE");
                
                try{
                    
                    /**String [] trxChannel = {"1", "2", "3", "4", "5", "19", "20", "11", "12", "13", "14", "16", "17", 
                                        "18", "21"};**/
                    String tempChannelType = rs.getString("CHANNEL_TYPE");
                    int channelType = 0;
                    
                    /**
                    //isnumeric()
                    if((ArrayUtils.contains(trxChannel, tempChannelType))){
                        channelType = rs.getInt("CHANNEL_TYPE");
                    }else{
                        channelType = 99;
                        channelTypeDesc = "Unknown Channel";
                    }*/
                    
                    if(rs.getString("CHANNEL_TYPE_DESC").equals("Unknown Channel"))
                    {
                        channelType = 99;
                        channelTypeDesc = "Unknown Channel";
                    }
                    else
                    {
                        channelType = rs.getInt("CHANNEL_TYPE");
                    }
                    
                    System.out.println("CHANNEL TYPE : " + channelTypeDesc );
                    System.out.println("TEMPLATE CODE : " + notifTemplateCode);

                
                    String messageTemplate = getMessageTemplate(conn, notifTemplateCode);
                    String sender = senderEmail(senderMB, senderIB, unknownSender, channelTypeDesc);
                    String passwordSender = passSenderEmail(passwordMB, passwordIB, passwordUS, channelTypeDesc);

                    LinkedHashMap<String, String> dbValues = getVariableTemplate(messageTemplate);


                    for (String column : dbValues.keySet()) {
                        //System.out.println(column);
                        //System.out.println(rs.getString("PAYEE_ROUTING_TYPE") + channelTypeDesc);
                        String mappedColumn = mappingColumn(column, trxMapping, trxType, channelType, rs.getString("PAYEE_ROUTING_TYPE"), channelTypeDesc);
                        String values = mappingValues(mappedColumn, rs.getString(mappedColumn));
                        dbValues.put(column, values);
                        //System.out.println(column + values);
                    }

                    System.out.println("-------EMAIL PARAMETER----------");

                    dbValues.entrySet().forEach(entry -> {
                            System.out.println(entry.getKey() + " " + entry.getValue());
                        });

                    String messageToSend = messageText(messageTemplate, dbValues);
                    System.out.println("Email Message :" + messageToSend);

                    logger.info("Processing email...");
                    logger.info("Email Message :" + messageToSend);
                    logger.info("CHANNEL TYPE : " + channelTypeDesc);
                    logger.info("TEMPLATE CODE : " + notifTemplateCode);
                
                    if(messageToSend.length() > 1){
                        boolean sendEmailStatus = sendEmail(messageText(messageTemplate, dbValues), notifTemplateCode, allRuleName, sender, decryptedPass(passwordSender),
                            recipients, hostname, port, auth, starttls, conn, trxKey);
                        if(sendEmailStatus == true){
                            updateSendStatus(conn, trxKey, trxMapping, actTrxKey);
                        }else{
                            updateSendStatusMessageError(conn, trxKey, trxMapping, actTrxKey);
                        }

                    }else{
                        updateSendStatusMessageNull(conn, trxKey, trxMapping, actTrxKey);
                        System.out.println("email will not send, message null");
                        logger.error("email will not send, message null");
                    }
                } catch (Exception e) {
                    System.out.println("System get error " + e.getMessage());
                    logger.error("System get error " + e.getMessage());
                    updateSendStatusMessageError(conn, trxKey, trxMapping, actTrxKey);
                }
                
                
                System.out.println("---------------------------------------------");
            }
            
            
            conn.close();    
        } catch (Exception e) {
            System.out.println("System get error " + e.getMessage());
            logger.error("System get error " + e.getMessage());
        }
    }
    
}
