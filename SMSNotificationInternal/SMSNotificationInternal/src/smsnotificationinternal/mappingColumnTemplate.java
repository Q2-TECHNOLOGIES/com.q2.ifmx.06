/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsnotificationinternal;

import java.util.LinkedHashMap;

/**
 *
 * @author FS40049X
 */
public class mappingColumnTemplate {
    
    public static String mappingSMSTemplate(String smsTemplate){
        LinkedHashMap<String, String> mapTemplate  = new LinkedHashMap<String, String>() {{
            put("SESSION",	"SMS009");
            put("NONTRX",	"SMS010");
            put("TRX",	"SMS011");
        }};
        String values ="";
        for (String template : mapTemplate.keySet()) {
            if(smsTemplate.equals(template)){
                values =  mapTemplate.get(template);
            }
        }
        return values;
    }
    
     public static String mappingColumn(String columnName){
        LinkedHashMap<String, String> mapColumn  = new LinkedHashMap<String, String>() {{
            put("RULE_NAME",	"RULE_NAME");
            put("PARTY_KEY",	"PARTY_KEY");
            put("ALERT_DATE",	"ALERT_DATE");
            put("SESSION_KEY",	"SESSION_KEY");
            put("CHANNEL_NAME",	"CHANNEL_TYPE_DESC");
            put("BENEFICIARY_CURENCY",	"BENEFICIARY_CURRENCY");//
            put("ORIGINAL_AMOUNT",	"REQUESTED_AMOUNT_AS_ENTERED");
            put("TRANSACTION_KEY",	"TRX_KEY");
            }};
        
        String values =columnName;
        for (String column : mapColumn.keySet()) {
            columnName = columnName.replace("[", "").replace("]", "");
            
            if(columnName.equals(column)){
                values =  mapColumn.get(column);
            }
        }
        return values;
    }
    
}
