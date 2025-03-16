/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  main.Decryptor
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package app;

import Common.Logging;
import Common.PropertiesLoader;
import ch.qos.logback.classic.LoggerContext;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.slf4j.LoggerFactory;


public class app {
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("app");
    public static PropertiesLoader pl;


    public static void main(String[] args) throws Exception {
        pl = new PropertiesLoader(args[0]);
        Properties prop = new Properties();
        Logging log = new Logging(); 
        log.configLog(pl,logger,loggerContext);
        
        String tpsType = pl.tpsType;
        String tpsTypeMonitoring = pl.tpsType + "_MONITORING";
        int totalMaximumTPS = pl.totalMaximumTPS;
        String serverIPConfig = pl.serverIPConfig;
        String db_host = pl.db_host;
        String db_port = pl.db_port;
        String db_username = pl.db_username;
        String db_name = pl.db_name;
        String db_password = pl.db_password;
        String sender = pl.sender;
        String passwordSender = pl.passwordSender;
        String[] recipients = pl.recipients.split(";");
        String hostname = pl.hostname;
        String port = pl.port;
        String auth = pl.auth;
        String starttls = pl.starttls;
        Connection conn = dbConn.getdbconn(db_host, db_port, db_username, db_name, db_password);
        try {
            logger.info("Starting IFMX TPS Monitoring - server " + serverIPConfig);
            String currentDate = dbController.getCurrentDate();
            logger.info("Current Date : " + currentDate);
            String currentTime = dbController.getCurrentTime();
            logger.info("Current Time : " + currentTime);
            LocalDate lastDate = LocalDate.parse(currentDate);
            LocalTime lastTime = LocalTime.parse(currentTime);
            
            System.out.println("Checking Log Row...");
            logger.info("Checking Log Row...");
            logger.info("TPS Type Monitoring : " + tpsTypeMonitoring);
            String check1 = dbController.getLastDate(conn, tpsTypeMonitoring);
            String check2 = dbController.getLastTime(conn, tpsTypeMonitoring);
            logger.info("Last Date : " + check1);
            logger.info("Last Time : " + check2);
            
            
            
            if (dbController.getLastDate(conn, tpsTypeMonitoring).equals("") && dbController.getLastTime(conn, tpsTypeMonitoring).equals("")) {
                boolean isSendEmail = false;
                HashMap<String, Integer> values = new HashMap<String, Integer>();
                List<String> serverIPList = dbController.checkingServerIPNA(conn);
                logger.info("List IP Server :" + serverIPList);
                for (String serverIP : serverIPList) {
                    if (dbController.checkingCountLogRowNA(conn, serverIP) <= 0) {
                        dbController.insertLogCountNA(conn, lastDate, lastTime, tpsTypeMonitoring, tpsType, serverIP);
                        logger.info("Data successfully added to table IMPL_LOG_MONITORING_BATCH_TRACKING");
                        continue;
                    }
                    List<Integer> countRow = dbController.checkingLogRowNA(conn, serverIP);
                    System.out.println("Maximum row detected " + Collections.max(countRow) + ", server IP " + serverIP);
                    logger.info("Maximum row detected " +Collections.max(countRow) +" From IMPL_IFMX_ACCESS_LOG, server IP "+ serverIP);
                    int maxOfRowValue = Collections.max(countRow);
                    if (maxOfRowValue > totalMaximumTPS) {
                        logger.info("Excess log limit server IP " + serverIP + " detected, processing send email ...");
                        dbController.insertLogCount(conn, lastDate, lastTime, tpsTypeMonitoring, tpsType, serverIP);
                        logger.info("Data successfully added to IMPL_LOG_MONITORING_BATCH_TRACKING");
                        values.put(serverIP, maxOfRowValue);
                        isSendEmail = true;
                        continue;
                    }
                    dbController.insertLogCountNA(conn, lastDate, lastTime, tpsTypeMonitoring, tpsType, serverIP);
                }
                if (isSendEmail) {
                    System.out.println("Send Email!!!");
                    //sendEmail.sendEmail(currentDate, currentTime, tpsType, serverIPConfig, values, totalMaximumTPS, sender, app.decryptedPass(passwordSender), recipients, hostname, port, auth, starttls);
                }
            } else {
                String currentDateDB = dbController.getLastDate(conn, tpsTypeMonitoring);
                String currentTimeDB = dbController.getLastTime(conn, tpsTypeMonitoring);
                LocalDate lastDateDB = LocalDate.parse(currentDateDB);
                LocalTime lastTimeDB = LocalTime.parse(currentTimeDB);
                System.out.println(lastDateDB + " " + lastTimeDB);
                logger.info("Last timestamp from table batch tracking " + lastDateDB + " " + lastTimeDB);
                System.out.println("Checking Log Row...");
                boolean isSendEmail = false;
                HashMap<String, Integer> values = new HashMap<String, Integer>();
                List<String> serverIPList = dbController.checkingServerIPNA(conn);
                for (String serverIP : serverIPList) {
                    if (dbController.checkingCountLogRow(conn, lastDateDB, lastTimeDB, serverIP) <= 0) {
                        dbController.insertLogCountNA(conn, lastDate, lastTime, tpsTypeMonitoring, tpsType, serverIP);
                        logger.info("Data successfully added to IMPL_LOG_MONITORING_BATCH_TRACKING");
                        continue;
                    }
                    List<Integer> countRow = dbController.checkingLogRow(conn, lastDateDB, lastTimeDB, serverIP);
                    System.out.println("Max of list is " + Collections.max(countRow));
                    logger.info("Max of list is " + Collections.max(countRow));
                    int maxOfRowValue = Collections.max(countRow);
                    if (maxOfRowValue > totalMaximumTPS) {
                        logger.info("Excess log limit server IP " + serverIP + " detected, processing send email ...");
                        dbController.insertLogCount(conn, lastDate, lastTime, tpsTypeMonitoring, tpsType, serverIP);
                        values.put(serverIP, maxOfRowValue);
                        isSendEmail = true;
                        continue;
                    }
                    dbController.insertLogCountNA(conn, lastDate, lastTime, tpsTypeMonitoring, tpsType, serverIP);
                    logger.info("Data successfully added to IMPL_LOG_MONITORING_BATCH_TRACKING");
                }
                if (isSendEmail) {
                    System.out.println("Send Email!!!");
                    //sendEmail.sendEmail(currentDate, currentTime, tpsType, serverIPConfig, values, totalMaximumTPS, sender, passwordSender, recipients, hostname, port, auth, starttls);
                }
            }
            logger.info("Finished run IFMX TPS Monitoring");
            conn.close();
        }
        catch (Exception e) {
            System.out.println("System get error : " + e.getMessage());
            logger.error("System get error : " + e.getMessage());
        }
    }
}
