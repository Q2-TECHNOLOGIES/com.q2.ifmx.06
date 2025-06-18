/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailnotification;

import static emailnotification.app.decryptedPass;
import static emailnotification.app.logger;
import static emailnotification.mappingColumnTemplate.mappingColumn;
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
import static emailnotification.messageContent.getMessageTemplate;
import static emailnotification.messageContent.getVariableTemplate;
import static emailnotification.messageContent.mappingValues;
import static emailnotification.messageContent.messageText;
import static emailnotification.sendEmail.sendEmail;
import static emailnotification.sendStatus.updateSendStatus;
import static emailnotification.sendStatus.updateSendStatusMessageError;
import static emailnotification.sendStatus.updateSendStatusMessageNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.isNumeric;
/**
 *
 * @author dika.septiyawianto
 */
public class testLoadBat {
    public static void main(String[] args) throws Exception {
        //System.out.println(args[0] + args[1] + args[2] + args[3] + args[4]);
//        String db_host = args[0];
        String db_host = null;
        Connection conn = null ;
        String line;
        String cmd = "D:\\ACTIMIZE\\Batch\\EmailNotification\\emailnotification_config.bat";
        String param = "myparam";  
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.start();
        System.out.println("DB_HOST >>> "+pb);
        
        Runtime runtime = Runtime.getRuntime();
        Process p = Runtime.getRuntime().exec(cmd);
        
//        p = Runtime.getRuntime().exec("cmd.exe /c echo %DB_HOST%");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        p.waitFor();
        System.out.println("DB_HOST >>> "+p.exitValue());
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
        db_host = System.getenv("DB_HOST");
        System.out.println("DB_HOST >>> "+db_host);
        input.close();
    }
}
