/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailnotification;

import java.util.LinkedHashMap;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author FS40049X
 */
public class mappingColumnTemplate {
    
    static String octoClickDestinationAccount(int trxType, int channelType, String payeeRoutingType){
        int [] trxTemplate_1 = {1, 25, 56};
        int [] trxTemplate_2 = {2, 55, 5};
        int [] trxTemplate_3 = {3, 4};
        int [] trxTemplate_4 = { 53, 54};
        
        if((channelType == 4) && (ArrayUtils.contains(trxTemplate_1, trxType))){//1, 25, 56
            return "PAYEE_ACCOUNT_KEY";
        }else if((channelType == 4) && (ArrayUtils.contains(trxTemplate_2, trxType))){ //2, 55, 5, 53, 54
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        }else if((channelType == 4) && (ArrayUtils.contains(trxTemplate_3, trxType))){//3, 4
            return "PAYEE_MANAGED_REFERENCE_NUMBER";
        }else if((channelType == 5) && (trxType == 77)){//77
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        }else if((channelType == 4) && (trxType == 78)){//78
            if(payeeRoutingType.equalsIgnoreCase("ACCOUNT NUMBER")){
                return "PAYEE_ALIAS_KEY";
            }else if (payeeRoutingType.equalsIgnoreCase("PROXY")){
                return "PAYEE_ALIAS";
            }
        }else if((channelType == 4) && (ArrayUtils.contains(trxTemplate_4, trxType))){//53, 54
            return "PAYEE_ACCOUNT_NUMBER";
        }      
        return "";
    }
    
    static String octoMobileDestinationAccount(int trxType, int channelType, String payeeRoutingType){
        int [] trxTemplate_1 = {1, 25, 41, 56, 64, 67};
        //updated on 2022/06/02 add 86, 89
        int [] trxTemplate_2 = {1, 25, 41, 56, 64, 67,71, 73, 75, 86, 89};  
        //updated on 2022/06/02 add 90
        int [] trxTemplate_3 = {2, 55, 65, 66, 72, 74, 76, 5, 90};
        //updated on 2022/06/02 add 88
        int [] trxTemplate_4 = {3, 4, 88};
        int [] trxTemplate_5 = {53, 54};
        
        if((channelType == 16) && (ArrayUtils.contains(trxTemplate_1, trxType))){//1, 25, 41, 56, 64, 67
            return "PAYEE_ACCOUNT_KEY";
        //updated on 2022/06/02 add channelType 35, 38
        //updated on 2022/07/11 exclude channelType 38
        }else if((channelType == 12 || channelType == 35) && (ArrayUtils.contains(trxTemplate_2, trxType))){//1, 25, 41, 56, 64, 67
            return "PAYEE_ACCOUNT_KEY";
        }else if((channelType == 16) && (trxType == 2)){
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        //updated on 2022/07/11 change channelType 39 to 35
        }else if((channelType == 12 || channelType == 35) && (ArrayUtils.contains(trxTemplate_3, trxType))){//2, 55, 65, 66, 72, 74, 76, 5, 53, 54
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        }else if((channelType == 16) && (ArrayUtils.contains(trxTemplate_4, trxType))){//3, 4
            return "PAYEE_MANAGED_REFERENCE_NUMBER";
        //updated on 2022/06/02 add channelType 37
        //updated on 2022/07/11 change channelType 37 to 35
        }else if((channelType == 12 || channelType == 35) && (ArrayUtils.contains(trxTemplate_4, trxType))){//3, 4
            return "PAYEE_MANAGED_REFERENCE_NUMBER";
        }else if((channelType == 21) && (trxType == 77)){
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        //updated on 2022/06/02 add channelType 36 & trxType 87
        //updated on 2022/07/11 change channelType 36 to 35
        }else if((channelType == 12 || channelType == 35) && (trxType == 78 || trxType == 87)){//78
            if(payeeRoutingType.equalsIgnoreCase("ACCOUNT NUMBER")){
                return "PAYEE_ALIAS_KEY";
            }else if (payeeRoutingType.equalsIgnoreCase("PROXY")){
                return "PAYEE_ALIAS";
            }
        }else if((channelType == 12) && (ArrayUtils.contains(trxTemplate_5, trxType))){//53, 54
            return "PAYEE_ACCOUNT_NUMBER";
        } 
        return "";
    }
    
    static String unknownChannelDestinationAccount(int trxType, int channelType, String payeeRoutingType){
        int [] trxTemplate_1 = {1, 25, 41, 56, 64, 67};
        //updated on 2022/06/02 add 86, 89
        int [] trxTemplate_2 = {1, 25, 41, 56, 64, 67,71, 73, 75, 86, 89};  
        //updated on 2022/06/02 add 90
        int [] trxTemplate_3 = {2, 55, 65, 66, 72, 74, 76, 5, 90};
        //updated on 2022/06/02 add 88
        int [] trxTemplate_4 = {3, 4, 88};
        int [] trxTemplate_5 = {53, 54};
        
        if((channelType == 99) && (ArrayUtils.contains(trxTemplate_1, trxType))){//1, 25, 41, 56, 64, 67
            return "PAYEE_ACCOUNT_KEY";
        }else if((channelType == 99) && (ArrayUtils.contains(trxTemplate_2, trxType))){//1, 25, 41, 56, 64, 67
            return "PAYEE_ACCOUNT_KEY";
        }else if((channelType == 99) && (trxType == 2)){
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        }else if((channelType == 99) && (ArrayUtils.contains(trxTemplate_3, trxType))){//2, 55, 65, 66, 72, 74, 76, 5, 53, 54
            return "PAYEE_DATA_ACCOUNT_NUMBER";
        }else if((channelType == 99) && (ArrayUtils.contains(trxTemplate_4, trxType))){//3, 4
            return "PAYEE_MANAGED_REFERENCE_NUMBER";
        }else if((channelType == 99) && (ArrayUtils.contains(trxTemplate_4, trxType))){//3, 4
            return "PAYEE_MANAGED_REFERENCE_NUMBER";
        }else if((channelType == 99) && (trxType == 77)){
            return "PAYEE_DATA_ACCOUNT_NUMBER";
         //updated on 2022/06/02 add trxType 87
        }else if((channelType == 99) && (trxType == 78 || trxType == 87)){//78
            if(payeeRoutingType.equalsIgnoreCase("ACCOUNT NUMBER")){
                return "PAYEE_ALIAS_KEY";
            }else if (payeeRoutingType.equalsIgnoreCase("PROXY")){
                return "PAYEE_ALIAS";
            }
        }else if((channelType == 99) && (ArrayUtils.contains(trxTemplate_5, trxType))){//53, 54
            return "PAYEE_ACCOUNT_NUMBER";
        } 
        return "";
    }
    
    public static String mappingColumn(String columnName, String emailTemplate, String trxType, int channelType, String payeeRoutingType, String channelTypeDesc){
        LinkedHashMap<String, String> mapColumn  = new LinkedHashMap<String, String>() {{
            put("RULE_NAME", "ALL_RULE_NAME");
            put("PARTY_KEY", "PARTY_KEY");
            put("SESSION_KEY", "SESSION_KEY");
            put("IP_ADDRESS", "WEB_SESSION_I_P_ADDRESS");
            put("TRANSACTION_KEY", "TRX_KEY");
            put("BENEFICIARY_CURRENCY", "BENEFICIARY_CURRENCY");
            put("ORIGINAL_AMOUNT", "REQUESTED_AMOUNT_AS_ENTERED");
            //put("DESTINATION_ACCOUNT", "PAYEE_ACCOUNT_KEY");//
            put("PAYEE_NAME", "PAYEE_NAME");
            put("CHANNEL_CODE", "CHANNEL_TYPE_DESC");
        }};
        
        String values =columnName;
        
        for (String column : mapColumn.keySet()) {
            columnName = columnName.replace("[", "").replace("]", "");
            
            if(columnName.equals("DESTINATION_ACCOUNT")){
                if(channelTypeDesc.equalsIgnoreCase("OCTO Clicks")){
                    values = octoClickDestinationAccount(Integer.parseInt(trxType), channelType, payeeRoutingType);
                
                }
                else if(channelTypeDesc.equalsIgnoreCase("OCTO Mobile")){
                    values = octoMobileDestinationAccount(Integer.parseInt(trxType), channelType, payeeRoutingType);
                
                }else if(channelTypeDesc.equalsIgnoreCase("Unknown Channel")){
                    values = unknownChannelDestinationAccount(Integer.parseInt(trxType), channelType, payeeRoutingType);
                    //System.out.println(values);
                }
                    
            }
            else if(columnName.equals(column)){
                values =  mapColumn.get(column);
            }
        }
        return values;
    }
    
}
