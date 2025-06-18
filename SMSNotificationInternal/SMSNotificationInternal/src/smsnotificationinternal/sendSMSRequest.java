/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsnotificationinternal;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import static smsnotificationinternal.app.logger;


/**
 *
 * @author FS40049X
 */
public class sendSMSRequest {
    
    public static String toJson(String templateCode, String phoneNum, String channelRefNo, Object [] parameterValue) {
        JSONObject jsonObject= new JSONObject();
        
        try {
          jsonObject.put("isFeePaid", "Y");
          jsonObject.put("isPremium", "N");
          jsonObject.put("sender", "02");
          jsonObject.put("phoneNum", phoneNum);
          jsonObject.put("templateCode", templateCode);
          jsonObject.put("parameterValue", parameterValue);
          jsonObject.put("channelRefNo", channelRefNo );
          return jsonObject.toString();
         
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return null;
    }
    
    public static boolean sendSMSRequest(String CIMB_API_Key, String SMS_Gateway_URL, String smsTemplate, 
                                String mobilePhone, String channelRefNo, Object [] dbValues, String requestID,
       String timestamp, int connTimeOut, int readTimeOut, int writeTimeOut) throws IOException{
        Boolean sendStatus = false;
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(connTimeOut, TimeUnit.SECONDS)
                            .readTimeout(readTimeOut, TimeUnit.SECONDS)
                            .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                            .build();
            
            MediaType mediaType = MediaType.parse("application/json"); //json type
            RequestBody body = RequestBody.create(mediaType, toJson(smsTemplate, mobilePhone,
                                                            channelRefNo ,dbValues));
            System.out.println("URL : " + SMS_Gateway_URL);
            logger.info("URL : " + SMS_Gateway_URL);
            System.out.println("CIMB API Key : " + CIMB_API_Key);
            logger.info("CIMB API Key : " + CIMB_API_Key);
            System.out.println("Request ID : " + requestID);
            logger.info("Request ID : " + requestID);
            System.out.println("Timestamp : " + timestamp); 
            logger.info("Timestamp : " + timestamp);
            System.out.println("Request Message : "+toJson(smsTemplate, mobilePhone,channelRefNo ,dbValues));
            logger.info("Request Message : "+toJson(smsTemplate, mobilePhone,channelRefNo ,dbValues));
            Request request = new Request.Builder()
                      .url(SMS_Gateway_URL)
                      .post(body)
                      .addHeader("CIMB-APIKey", CIMB_API_Key)
                      .addHeader("Request-ID", requestID)
                      .addHeader("CIMB-Timestamp", timestamp)
                      .addHeader("content-type", "application/json")
                      .addHeader("accept", "application/json")
                      .build();

            okhttp3.Response response = client.newCall(request).execute();
            System.out.println(response);
            System.out.println("Response Code : "+response.code());
            logger.info("Response Code : "+response.code());
            System.out.println("Response Message : "+response.message());
            logger.info("Response Message : "+response.message());

            if(response.code() == 200){
                sendStatus = true;
            }
            return sendStatus;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
        }
        return sendStatus;
    }
}
