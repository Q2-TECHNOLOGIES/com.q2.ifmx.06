/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package app;

import Common.PropertiesLoader;
import Common.Logging;
import ch.qos.logback.classic.LoggerContext;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public class dbController {
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("dbController");
    public static PropertiesLoader pl;

    public static String getLatestDateTime(Connection conn, String serverIP) throws SQLException {
        String latestDateTime = "";
        PreparedStatement preparedStatement = null;
        String strQuery = "select LATEST_DATETIME from idb_data_user.V_IMPL_IFMX_ACCESS_LOG_LOADER where SERVER_IP = ? order by LATEST_DATETIME desc LIMIT 1";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            latestDateTime = rs.getString("LATEST_DATETIME");
        }
        return latestDateTime;
    }

    public static String getEarlyDateTime(Connection conn, String serverIP) throws SQLException {
        String earlyDateTime = "";
        PreparedStatement preparedStatement = null;
        String strQuery = "SELECT (LATEST_DATETIME - INTERVAL '30 seconds') AS EARLY_DATETIME FROM idb_data_user.V_IMPL_IFMX_ACCESS_LOG_LOADER where SERVER_IP = ? order by LATEST_DATETIME desc LIMIT 1";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            earlyDateTime = rs.getString("EARLY_DATETIME");
        }
        return earlyDateTime;
    }

    public static HashMap<String, String> trxIDList(Connection conn, String latestDateTime, String earlyDateTime, String serverIP) throws SQLException {
        HashMap<String, String> trxIdMapForFilter = new HashMap<String, String>();
        PreparedStatement preparedStatement = null;
        String strQuery = "select TRANSACTION_ID from idb_data_user.V_IMPL_IFMX_ACCESS_LOG_LOADER where SERVER_IP = ? and LATEST_DATETIME between CAST(? AS TIMESTAMP) and CAST(? AS TIMESTAMP)";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, serverIP);
        preparedStatement.setString(2, earlyDateTime);
        preparedStatement.setString(3, latestDateTime);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            trxIdMapForFilter.put(rs.getString("TRANSACTION_ID"), rs.getString("TRANSACTION_ID"));
        }
        return trxIdMapForFilter;
    }

    public static Integer checkingCountLogRow(Connection conn, String serverIP) throws SQLException {
        int result = 0;
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select count(*) as COUNT_ROW from idb_data_user.IMPL_IFMX_ACCESS_LOG where SERVER_IP = ?";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            result = rs.getInt("COUNT_ROW");
        }
        return result;
    }

    public static Integer insertStatus(Connection conn, String dataDateTime, String earlyDateTime) throws SQLException {
        int result = 0;
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "SELECT CAST(CASE WHEN ? > ? THEN 1 ELSE 0 END AS BIT) AS INSERT_STATUS";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, dataDateTime);
        preparedStatement.setString(2, earlyDateTime);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            result = rs.getInt("INSERT_STATUS");
        }
        return result;
    }  
    
    public static void insertData1(Connection conn, String serverIP, Pattern pattern, Pattern pattern2, String data, DateTimeFormatter df, String earlyDateTime, HashMap<String, String> trxIdMapForFilter) throws SQLException {
        Matcher messages = pattern.matcher(data);
        Matcher messages2 = pattern2.matcher(data);
        String dbInputStatus = "";
        String dbRPTInputStatus = "";
        if (messages.matches()) {
            System.out.println(messages.group(0));
            System.out.println("-1-  " + messages.group(1));
            System.out.println("-2-  " + messages.group(2));
            System.out.println("-3-  " + messages.group(3));
            System.out.println("-4-  " + messages.group(4));
            System.out.println("-5-  " + messages.group(5));
            System.out.println("-6-  " + messages.group(6));
            System.out.println("-7-  " + messages.group(7));
            System.out.println("-8-  " + messages.group(8));
            System.out.println("-9-  " + messages.group(9));
            System.out.println("-10-  " + messages.group(10));
            System.out.println("-11-  " + messages.group(11));
            System.out.println("-12-  " + messages.group(12));
            System.out.println("-13-  " + messages.group(13));
            System.out.println("-14-  " + messages.group(14));
            System.out.println("-15-  " + messages.group(15));
            LocalDate dbDate = LocalDate.parse(messages.group(2), df);
            String arrivalTime = messages.group(2) + " " + messages.group(3);
            String dataTimeStamp = dbDate + " " + messages.group(3);
            System.out.println("data timestamp " + dataTimeStamp);
            String trxID = messages.group(7).replace("[", "").replace("]", "");
            boolean isNewTransactionData = false;
            if (trxIdMapForFilter.get(trxID) == null) {
                isNewTransactionData = true;
            }
            Integer insertStatusDate = dbController.insertStatus(conn, dataTimeStamp, earlyDateTime);
            if (isNewTransactionData && insertStatusDate == 1) {
                dbInputStatus = dbController.insertToDB(conn, messages.group(3), dbDate, messages.group(0).substring(1), messages.group(15), messages.group(4), messages.group(5), messages.group(6), serverIP, messages.group(7));
                dbRPTInputStatus = dbController.insertToDBRPT(conn, messages.group(3), dbDate, messages.group(0).substring(1), messages.group(15), messages.group(4), messages.group(5), messages.group(6), serverIP, arrivalTime, messages.group(7), messages.group(8), messages.group(9), messages.group(10), messages.group(11), messages.group(12), messages.group(13), messages.group(14), messages.group(15));
            } else {
                dbInputStatus = "Data not inserted";
            }
        } else if (messages2.matches()) {
            System.out.println(messages2.group(0));
            System.out.println("-1-  " + messages2.group(1));
            System.out.println("-2-  " + messages2.group(2));
            System.out.println("-3-  " + messages2.group(3));
            System.out.println("-4-  " + messages2.group(4));
            System.out.println("-5-  " + messages2.group(5));
            System.out.println("-6-  " + messages2.group(6));
            System.out.println("-7-  " + messages2.group(7));
            System.out.println("-8-  " + messages2.group(8));
            System.out.println("-9-  " + messages2.group(9));
            System.out.println("-10-  " + messages2.group(10));
            System.out.println("-11-  " + messages2.group(11));
            System.out.println("-12-  " + messages2.group(12));
            System.out.println("-13-  " + messages2.group(13));
            System.out.println("-14-  " + messages2.group(14));
            LocalDate dbDate = LocalDate.parse(messages2.group(1), df);
            String arrivalTime = messages2.group(1) + " " + messages2.group(2);
            String dataTimeStamp = dbDate + " " + messages2.group(2);
            System.out.println("data timestamp " + dataTimeStamp);
            String trxID = messages2.group(6).replace("[", "").replace("]", "");
            boolean isNewTransactionData = false;
            if (trxIdMapForFilter.get(trxID) == null) {
                isNewTransactionData = true;
            }
            Integer insertStatusDate = dbController.insertStatus(conn, dataTimeStamp, earlyDateTime);
            if (isNewTransactionData && insertStatusDate == 1) {
                dbInputStatus = dbController.insertToDB(conn, messages2.group(2), dbDate, messages2.group(0), messages2.group(14), messages2.group(3), messages2.group(4), messages2.group(5), serverIP, messages2.group(6));
                dbRPTInputStatus = dbController.insertToDBRPT(conn, messages2.group(2), dbDate, messages2.group(0), messages2.group(14), messages2.group(3), messages2.group(4), messages2.group(5), serverIP, arrivalTime, messages2.group(6), messages2.group(7), messages2.group(8), messages2.group(9), messages2.group(10), messages2.group(11), messages2.group(12), messages2.group(13), messages2.group(14));
            } else {
                dbInputStatus = "Data not inserted";
            }
        } else {
            dbInputStatus = "Data does not match with the pattern";
        }
        System.out.println(dbInputStatus);
        System.out.println(dbRPTInputStatus);
        System.out.println("----------------------------------");
    }
    
    public static String insertToDB(Connection conn, String time, LocalDate date, String data, String status, String hour, String minute, String second, String serverIP, String transactionID) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO idb_data_user.IMPL_IFMX_ACCESS_LOG VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        
        int hourInt = Integer.parseInt(hour);
        int minuteInt = Integer.parseInt(minute);
        int secondInt = Integer.parseInt(second);
        
        LocalTime crrtime = LocalTime.parse(time);
        
        prepstmt.setTime(1, Time.valueOf(crrtime));
        prepstmt.setDate(2, Date.valueOf(date));
        prepstmt.setString(3, data);
        prepstmt.setString(4, status);
        prepstmt.setInt(5, hourInt);  // Assuming log_hour is numeric, e.g., INTEGER
        prepstmt.setInt(6, minuteInt);  // Assuming log_minute is numeric, e.g., INTEGER
        prepstmt.setInt(7, secondInt);  // Assuming log_second is numeric, e.g., INTEGER
        prepstmt.setString(8, serverIP);
        prepstmt.setString(9, transactionID.replace("[", "").replace("]", ""));
        prepstmt.executeUpdate();
        prepstmt.close();
        return "Data successfully added!";
    }
    
    public static String insertToDBRPT(Connection conn, String time, LocalDate date, String data, String status, String hour, String minute, String second, String serverIP, String transactionArrivalTime, String transactionID, String responseTime, String timeInFlow, String status2, String processThreadId, String webServerGlobalReqNumber, String webServerTotalResponseTime, String webServerThreadId, String webServerStatus) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO idb_data_user.IMPL_IFMX_RPT_ACCESS_LOG VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        
        int hourInt = Integer.parseInt(hour);
        int minuteInt = Integer.parseInt(minute);
        int secondInt = Integer.parseInt(second);
        
        LocalTime crrtime = LocalTime.parse(time);
        
        prepstmt.setTime(1, Time.valueOf(crrtime));
        prepstmt.setDate(2, Date.valueOf(date));
        prepstmt.setString(3, data);
        prepstmt.setString(4, status);
        prepstmt.setInt(5, hourInt);  // Assuming log_hour is numeric, e.g., INTEGER
        prepstmt.setInt(6, minuteInt);  // Assuming log_minute is numeric, e.g., INTEGER
        prepstmt.setInt(7, secondInt);  // Assuming log_second is numeric, e.g., INTEGER
        prepstmt.setString(8, serverIP);
        prepstmt.setString(9, transactionArrivalTime);
        prepstmt.setString(10, transactionID.replace("[", "").replace("]", ""));
        prepstmt.setString(11, responseTime);
        prepstmt.setString(12, timeInFlow);
        prepstmt.setString(13, status2);
        prepstmt.setString(14, processThreadId);
        prepstmt.setString(15, webServerGlobalReqNumber.replace("[", "").replace("]", ""));
        prepstmt.setString(16, webServerTotalResponseTime);
        prepstmt.setString(17, webServerThreadId);
        prepstmt.setString(18, webServerStatus);
        prepstmt.executeUpdate();
        prepstmt.close();
        return "Data RPT successfully added!";
    }
    
    public static void insertData2(Connection conn, String serverIP, Pattern pattern, Pattern pattern2, String data, DateTimeFormatter df) throws SQLException {
        Matcher messages = pattern.matcher(data);
        Matcher messages2 = pattern2.matcher(data);
        String dbInputStatus = "";
        String dbRPTInputStatus = "";
        if (messages.matches()) {
            System.out.println(messages.group(0));
            System.out.println("-1-  " + messages.group(1));
            System.out.println("-2-  " + messages.group(2));
            System.out.println("-3-  " + messages.group(3));
            System.out.println("-4-  " + messages.group(4));
            System.out.println("-5-  " + messages.group(5));
            System.out.println("-6-  " + messages.group(6));
            System.out.println("-7-  " + messages.group(7));
            System.out.println("-8-  " + messages.group(8));
            System.out.println("-9-  " + messages.group(9));
            System.out.println("-10-  " + messages.group(10));
            System.out.println("-11-  " + messages.group(11));
            System.out.println("-12-  " + messages.group(12));
            System.out.println("-13-  " + messages.group(13));
            System.out.println("-14-  " + messages.group(14));
            System.out.println("-15-  " + messages.group(15));
            LocalDate dbDate = LocalDate.parse(messages.group(2), df);
            String arrivalTime = messages.group(2) + " " + messages.group(3);
            dbInputStatus = dbController.insertToDB(conn, messages.group(3), dbDate, messages.group(0).substring(1), messages.group(15), messages.group(4), messages.group(5), messages.group(6), serverIP, messages.group(7));
            dbRPTInputStatus = dbController.insertToDBRPT(conn, messages.group(3), dbDate, messages.group(0).substring(1), messages.group(15), messages.group(4), messages.group(5), messages.group(6), serverIP, arrivalTime, messages.group(7), messages.group(8), messages.group(9), messages.group(10), messages.group(11), messages.group(12), messages.group(13), messages.group(14), messages.group(15));
        } else if (messages2.matches()) {
            System.out.println(messages2.group(0));
            System.out.println("-1-  " + messages2.group(1));
            System.out.println("-2-  " + messages2.group(2));
            System.out.println("-3-  " + messages2.group(3));
            System.out.println("-4-  " + messages2.group(4));
            System.out.println("-5-  " + messages2.group(5));
            System.out.println("-6-  " + messages2.group(6));
            System.out.println("-7-  " + messages2.group(7));
            System.out.println("-8-  " + messages2.group(8));
            System.out.println("-9-  " + messages2.group(9));
            System.out.println("-10-  " + messages2.group(10));
            System.out.println("-11-  " + messages2.group(11));
            System.out.println("-12-  " + messages2.group(12));
            System.out.println("-13-  " + messages2.group(13));
            System.out.println("-14-  " + messages2.group(14));
            LocalDate dbDate = LocalDate.parse(messages2.group(1), df);
            String arrivalTime = messages2.group(1) + " " + messages2.group(2);
            dbInputStatus = dbController.insertToDB(conn, messages2.group(2), dbDate, messages2.group(0), messages2.group(14), messages2.group(3), messages2.group(4), messages2.group(5), serverIP, messages2.group(6));
            dbRPTInputStatus = dbController.insertToDBRPT(conn, messages2.group(2), dbDate, messages2.group(0), messages2.group(14), messages2.group(3), messages2.group(4), messages2.group(5), serverIP, arrivalTime, messages2.group(6), messages2.group(7), messages2.group(8), messages2.group(9), messages2.group(10), messages2.group(11), messages2.group(12), messages2.group(13), messages2.group(14));
        } else {
            dbInputStatus = "Data does not match with the pattern";
        }
        System.out.println(dbInputStatus);
        System.out.println(dbRPTInputStatus);
        System.out.println("----------------------------------");
    }
}
