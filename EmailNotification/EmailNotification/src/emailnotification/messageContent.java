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
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author FS40049X
 */
public class messageContent {
    
        public static String getMessageTemplate(Connection conn, String dbTemplate) throws SQLException{
        String temp = "";
        ResultSet rs;
        PreparedStatement preparedStatement = null;
        //String strQuery = "select * from dbo.EMAIL_TEMPLATE where EMAIL_TEMPLATE_CODE = ?" ;
        String strQuery = "select * from IDB..V_IMPL_EMAIL_NOTIFICATION_TEMPLATE where EMAIL_TEMPLATE_CODE = ?";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, dbTemplate);
        rs = preparedStatement.executeQuery();

        while(rs.next()) {
            temp = rs.getString("EMAIL_TEMPLATE");
        }
        return temp;
    }
    
    public static LinkedHashMap<String, String> getVariableTemplate(String temp) throws SQLException{
        LinkedHashMap<String, String> varTemplate = new LinkedHashMap<String, String>();
        
        String[] splitted = temp.split("(<br>)|\\s");
        String regexPattern = "(\\[.*\\])";
        final Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL | Pattern.MULTILINE);
                
        for(String a: splitted){
            //System.out.println(a);
            if(a.matches(regexPattern)){
                Matcher messages = pattern.matcher(a);
                messages.matches();
                //System.out.println(messages.group(0)+ "     column:"+ messages.group(1));
                varTemplate.put(messages.group(1), "");
            }
        }
        return varTemplate;
    }
    
    
    public static String messageText(String messageTemplate, LinkedHashMap<String, String> data){
        String[] splitted = messageTemplate.split(" ");
        StringBuilder emailMessage = new StringBuilder();
        
        String regexPattern = "(\\[.*\\])";
        final Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL | Pattern.MULTILINE);
        for(String a: splitted){
            //System.out.println(a);
            if(a.matches(regexPattern)){
                Matcher messages = pattern.matcher(a);
                messages.matches();
                emailMessage.append(" " + data.get(a));
            }else if(a.contains("<br>")){
                String[] splittedbr = a.split("<br>");
                for(String b: splittedbr){
                    if(b.matches(regexPattern)){
                        Matcher messages2 = pattern.matcher(b);
                        messages2.matches();
                        emailMessage.append(" " + data.get(b));                        
                    }else{
                        emailMessage.append("<br>" + b);
                    }}
            }else{
                emailMessage.append(" " + a);
            }
        }
        return emailMessage.toString();
    }
    
    public static String mappingValues(String column, String values){
        //System.out.println(column + " : "+ values);
        if(column.equals("REQUESTED_AMOUNT_AS_ENTERED")){
            Double vals;
            if(values == null){
                values = "0";
            }
            vals = Double.parseDouble(values);
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
            String numValues = nf.format(vals);
            //numValues = numValues.replace('.',','); //.concat(".00");
            return numValues;
        }else if(column.equalsIgnoreCase("TRX_KEY")){
            return values.replace("default", "");
        }else{
            return values;
        }
    }
}
