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
import app.AccessLogData;
import app.dbConn;
import app.dbController;
import ch.qos.logback.classic.LoggerContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public class app {
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("app");
    public static PropertiesLoader pl;
    final String delimiter = "\t";
    String[] splitted = new String[64];
    ArrayList<AccessLogData> listObj = new ArrayList();

    public static void main(String[] args) throws IOException, Exception {
        Properties prop = new Properties(); 
        pl = new PropertiesLoader(args[0]);
        Logging log = new Logging(); 
        log.configLog(pl,logger,loggerContext);
        
        
        String filePath = pl.filePath;
        String serverIP = pl.serverIP;
        String db_host = pl.db_host;
        String db_port = pl.db_port;
        String db_username = pl.db_username;
        String db_name = pl.db_name;
        String db_password = pl.db_password;
        String filePathTemp = filePath + "/temp/";
        ArrayList<String> filesInFolder = new ArrayList<String>();

        try {
            File[] files;
            File folder = new File(filePath);
            for (File file : files = folder.listFiles()) {
                if (!file.isFile() || !file.toString().contains("Custom.access.")) continue;
                Files.createDirectories(Paths.get(filePathTemp, new String[0]), new FileAttribute[0]);
                Files.copy(Paths.get(file.toString(), new String[0]), Paths.get(filePathTemp + file.getName(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                filesInFolder.add(filePathTemp + file.getName());
                logger.info("Find file " + file.getName() + " ...");
                logger.info("Copy file " + file.getName() + " to folder temp");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("System get error : " + e.getCause());
            logger.error("System get error : " + e.getCause());
        }
        Connection conn = dbConn.getdbconn(db_host, db_port, db_username, db_name, db_password);
        String str_latestDateTime = dbController.getLatestDateTime(conn, serverIP);
        String str_earlyDateTime = dbController.getEarlyDateTime(conn, serverIP);
        
        
        for (String files : filesInFolder) {
            logger.info("Processing file " + files + " ...");
            String regexPattern = "^(.{1})(\\d{4}-.{3}-\\d{2})\\s((\\d{2}):(\\d{2}):(\\d{2})\\.\\d{3})\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)";
            String regexPattern2 = "^(\\d{4}-.{3}-\\d{2})\\s((\\d{2}):(\\d{2}):(\\d{2})\\.\\d{3})\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)";
            Pattern pattern = Pattern.compile(regexPattern, 40);
            Pattern pattern2 = Pattern.compile(regexPattern2, 40);
            System.out.println("-------------" + files + "-------------");
            ArrayList<String> result = new ArrayList<String>();
            try (BufferedReader br = Files.newBufferedReader(Paths.get(files, new String[0]));){
                String line;
                while ((line = br.readLine()) != null) {
                    result.add(line);
                }
            }
            catch (IOException e) {
                System.out.println(e.getCause());
            }
            System.out.println("----------------------------------");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
            try {
                System.out.println("Latest Datetime : " + str_latestDateTime);
                System.out.println("Early 30 Seconds Datetime : " + str_earlyDateTime);
                if (!str_latestDateTime.equals("")) {
                    logger.info("Latest timestamp from database : " + str_latestDateTime);
                    HashMap<String, String> trxIdMapForFilter = dbController.trxIDList(conn, str_latestDateTime, str_earlyDateTime, serverIP);
                    for (int i = 0; i < result.size(); ++i) {
                        String data = (String)result.get(i);
                        dbController.insertData1(conn, serverIP, pattern, pattern2, data, df, str_earlyDateTime, trxIdMapForFilter);
                    }
                } else {
                    for (int i = 0; i < result.size(); ++i) {
                        String data = (String)result.get(i);
                        dbController.insertData2(conn, serverIP, pattern, pattern2, data, df);
                    }
                }
                logger.info("Finished processing insert data to database");
            }
            catch (Exception e) {
                System.out.println("System get error" + e.getMessage());
            }
        }
        conn.close();
    }
}
