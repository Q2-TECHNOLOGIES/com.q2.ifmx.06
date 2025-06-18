/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsnotificationinternal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 *
 * @author FS40049X
 */
public class messageContent {
    
     public static String mappingValues(String column, String values){
        //System.out.println(column + values);
        if(values == null || values.isEmpty()){
            return "";
        }else if(column.equals("ALERT_DATE")){
            DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS");
            DateTimeFormatter convertedOutputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           
            LocalDateTime starting = LocalDateTime.parse(values, currentFormatter);
            return starting.format(convertedOutputFormatter);
        }else if(column.equals("CHANNEL_TYPE_DESC")){
            if(values.equalsIgnoreCase("OCTO Clicks")){
                return "IB";
            }else if(values.equalsIgnoreCase("OCTO Mobile")){
                return "MB";
            }else if(values.equalsIgnoreCase("Unknown Channel")){
                return "CH";
            }
        }else if(column.equalsIgnoreCase("TRX_KEY")&& values != null){
                String tempvalue =  values.replace("default", "");  
                if(tempvalue.length() >20){
                    return tempvalue.substring(tempvalue.length()-20,tempvalue.length());
                }else{
                    return tempvalue;
                }
                
        }else if(column.equalsIgnoreCase("RULE_NAME")&& values != null && values.length() >50){
            return values.substring(0,50);
            
        }else if(column.equalsIgnoreCase("SESSION_KEY")&& values != null && values.length() >20){
                return values.substring(values.length()-20,values.length());   
        }else if(column.equals("REQUESTED_AMOUNT_AS_ENTERED") && values != null){
            if(values == ""){
                values = "0";
            }
            Double vals;
            vals = Double.parseDouble(values);
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
            String amount = nf.format(vals);
            return(amount);
        
        }
        return values;
    }
     public static LinkedHashMap<String, String> getVariableTemplate(Connection conn, String dbTemplate) throws SQLException{
        LinkedHashMap<String, String> varTemplate = new LinkedHashMap<String, String>();
        String temp = "";
        
        ResultSet rs;
        PreparedStatement preparedStatement = null;
        String strQuery = "select * from IDB.dbo.V_IMPL_SMS_NOTIFICATION_TEMPLATE where SMS_TEMPLATE_CODE = ?" ;
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, dbTemplate);
        rs = preparedStatement.executeQuery();

        while(rs.next()) {
            temp = rs.getString("SMS_TEMPLATE");
        }
        
        String[] splitted = temp.split("~");
        for(String a: splitted){
            varTemplate.put(a, "");
        }
        
        return varTemplate;
    }
    
}
