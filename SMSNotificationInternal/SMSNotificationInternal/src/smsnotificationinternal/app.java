/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsnotificationinternal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import main.Decryptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static smsnotificationinternal.sendStatus.updateSendStatusTemplate;
import static smsnotificationinternal.sendStatus.updateSendStatusError;
import static smsnotificationinternal.sendStatus.updateSendStatus;
import static smsnotificationinternal.sendStatus.updateSendStatusMaximum;
import static smsnotificationinternal.trackingSMSInternal.checkingCountTrackingTable;
import static smsnotificationinternal.trackingSMSInternal.insertToTrackingTable;
import static smsnotificationinternal.sendSMSRequest.sendSMSRequest;
import static smsnotificationinternal.mappingColumnTemplate.mappingColumn;
import static smsnotificationinternal.mappingColumnTemplate.mappingSMSTemplate;
import static smsnotificationinternal.messageContent.getVariableTemplate;
import static smsnotificationinternal.messageContent.mappingValues;

/**
 *
 * @author rizaac
 */
public class app {
    
    final static Logger logger = LogManager.getLogger(app.class);
    
    public static String getCurrentDateTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmSS");  
        LocalDateTime now = LocalDateTime.now(); 
        return dtf.format(now);
    }
    
    public static String generateRequestID(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return RandomStringUtils.randomNumeric(4) + timeStamp.substring(1, timeStamp.length()) + RandomStringUtils.randomNumeric(7);
        
    }
    
    public static String generateTimestamp(){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String formattedDate = f.format(new Date());
        return formattedDate;
    }
    
    public static String decryptedPass(String password) throws Exception{
        Decryptor dec = new Decryptor();
        if(password.isEmpty()){
            return password;
        }else{
            return dec.decrypt(password);
        }
    }
    public static void main(String[] args) throws Exception {
        String db_host = args[0];
        String db_port = args[1];
        String db_username = args[2]; 
        String db_name = args[3]; 
        String db_password = args[4];
        String CIMB_API_Key = args[5];
        String SMS_Gateway_URL = args[6];
        int Max_Send = Integer.valueOf(args[7]);
        String phoneNum = args[8];
        String [] phoneNumRecipients = phoneNum.split(";");
        int connTimeOut = Integer.valueOf(args[9]);
        int readTimeOut = Integer.valueOf(args[10]);
        int writeTimeOut = Integer.valueOf(args[11]);
        
        
        Connection conn = dbConn.getdbconn(db_host, db_port, db_username, db_name, decryptedPass(db_password));
        
        try {
            ResultSet rs;
            PreparedStatement preparedStatement = null;
            String strQuery = "select * from IDB.dbo.V_IMPL_SMS_ALERT_NOTIFICATION_INTERNAL where SEND_STATUS = ? and SMS_SEND_TYPE = ?" ; 
            preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, "0");
            preparedStatement.setString(2, "I");
            
            rs = preparedStatement.executeQuery();
            
            
            while(rs.next()) {
                String templateMessage = rs.getString("NOTIF_TEMPLATE_CODE");
                String channelType = rs.getString("CHANNEL_TYPE_DESC");
                String partyID = rs.getString("PARTY_ID");
                String ruleName = rs.getString("ALL_RULE_NAME");
                String message = rs.getString("TRX_KEY");
                String smsTemplateESB = mappingSMSTemplate(templateMessage);
                String trxKey = rs.getString("TRX_KEY");
                String actTrxKey = rs.getString("ACT_TRX_KEY");
                CIMB_API_Key = rs.getString("CIMB_API_KEY");

                //System.out.println(java.sql.Date.valueOf(LocalDate.now()));
                System.out.println("Channel Type : " + channelType);
                System.out.println("SMS Template : " + templateMessage);
                
                String [] template = {"SESSION", "TRX", "NONTRX"};
                try{
                
                    if(!ArrayUtils.contains(template, templateMessage)){
                        updateSendStatusTemplate(conn, trxKey, actTrxKey);
                        continue;
                    }

                    System.out.println("SMS Template ESB : " + smsTemplateESB);
                    LinkedHashMap<String, String> dbValues = getVariableTemplate(conn, templateMessage);

                
                    for (String column : dbValues.keySet()) {
                        String mappedColumn = mappingColumn(column);
                        String values = mappingValues(mappedColumn, rs.getString(mappingColumn(column)));//rs.getString(mappingColumn(column));
                        dbValues.put(column, values);
                        System.out.println(column + values);
                    }

                    String channelRefNo = "SMS_EXT_" + getCurrentDateTime();

                    Object [] dbVals = dbValues.values().toArray();

                
                //System.out.println(checkingCountTrackingTable(conn, partyID, ruleName));
                //System.out.println(partyID);
                    if(checkingCountTrackingTable(conn, partyID, ruleName) < Max_Send){
                        logger.info("Channel Type : " + channelType);
                        System.out.println("data valid, processing send sms...");
                        List<String> sendSMSstatus = new ArrayList<>();
                        for (String phoneNumber : phoneNumRecipients){
                            logger.info("processing send sms to " + phoneNumber + "...");
                            boolean sendSMS =sendSMSRequest(CIMB_API_Key, SMS_Gateway_URL, smsTemplateESB, phoneNumber,
                                                            channelRefNo , dbVals, generateRequestID(),generateTimestamp(),
                                                            connTimeOut, readTimeOut, writeTimeOut);
                            if(sendSMS == true){
                                System.out.println("Sucessfully send SMS to " + phoneNumber);
                                logger.info("Sucessfully send SMS to " + phoneNumber);
                                sendSMSstatus.add("send");
                            }else{
                                System.out.println("an error occured, sms will not send to " + phoneNumber);
                                logger.error("an error occured, sms will not send to " + phoneNumber);
                                sendSMSstatus.add("not send");

                            }
                        }

                        if(sendSMSstatus.contains("send")){
                            insertToTrackingTable(conn, partyID, ruleName, message);
                            updateSendStatus(conn, trxKey, actTrxKey);
                        }else{
                            updateSendStatusError(conn, trxKey, actTrxKey);
                            System.out.println("data not valid, sms will not send");
                            logger.error("data not valid, sms will not send");
                        }
                    }else{
                        updateSendStatusMaximum(conn, trxKey, actTrxKey);
                        System.out.println("maximum send message has been reached");
                        logger.info("maximum send message has been reached, sms will not send");
                    }
                }
                 catch (Exception e) {
                    System.out.println("System get error " + e.getMessage());
                    logger.error("System get error " + e.getMessage());
                    updateSendStatusError(conn, trxKey, actTrxKey);
                }
            }    
            conn.close();    
            } catch (Exception e) {
                System.out.println("System get error " + e.getMessage());
                logger.info("System get error " + e.getMessage());
            }
    }
    
}