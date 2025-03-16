/*
 * Decompiled with CFR 0.152.
 */
package app;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class dbController {
    
    public static String getCurrentDate() {
        DateTimeFormatter dDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        return dDate.format(localDate);
    }

    public static String getCurrentTime() {
        DateTimeFormatter dTime = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        LocalTime localTime = LocalTime.now();
        return dTime.format(localTime);
    }

    public static String getLastDate(Connection conn, String tpsTypeMonitoring) throws SQLException {
        String lastDate = "";
        PreparedStatement preparedStatement = null;
        String strQuery = "select BATCH_DATE from idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING WHERE BATCH_TYPE = ?\norder by BATCH_DATE desc, BATCH_TIME desc LIMIT 1";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, tpsTypeMonitoring);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            lastDate = rs.getString("BATCH_DATE");
            // Jika nilai lastDate adalah NULL, set ke string kosong
            if (lastDate == null) {
                lastDate = "";  // Ganti dengan string kosong jika NULL
            }
        }

        return lastDate;
    }

    public static String getLastTime(Connection conn, String tpsTypeMonitoring) throws SQLException {
        String lastTime = "";
        PreparedStatement preparedStatement = null;
        String strQuery = "select BATCH_TIME from idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING WHERE BATCH_TYPE = ? \norder by BATCH_DATE desc, BATCH_TIME desc LIMIT 1";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, tpsTypeMonitoring);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            lastTime = rs.getString("BATCH_TIME");
            // Jika nilai lastDate adalah NULL, set ke string kosong
            if (lastTime == null) {
                lastTime = "";  // Ganti dengan string kosong jika NULL
            }
        }

        return lastTime;
    }

    public static Integer checkingCountLogRow(Connection conn, LocalDate lastDateDB, LocalTime lastTimeDB, String serverIP) throws SQLException {
        int result = 0;
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select count(*)\nfrom idb_data_user.IMPL_IFMX_ACCESS_LOG where  LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00'::time else ? end) and SERVER_IP = ?\n";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setDate(1, Date.valueOf(lastDateDB));
        preparedStatement.setDate(2, Date.valueOf(lastDateDB));
        preparedStatement.setTime(3, Time.valueOf(lastTimeDB));
        preparedStatement.setString(4, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            result = rs.getInt(1);
        }
        return result;
    }

    public static Integer checkingCountLogRowNA(Connection conn, String serverIP) throws SQLException {
        int result = 0;
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select count(*)\nfrom idb_data_user.IMPL_IFMX_ACCESS_LOG where SERVER_IP = ?\n";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            result = rs.getInt(1);
        }
        return result;
    }

    public static List<Integer> checkingLogRowNA(Connection conn, String serverIP) throws SQLException {
        ArrayList<Integer> countRow = new ArrayList<Integer>();
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select LOG_HOUR, LOG_MINUTES, LOG_SECOND, LOG_DATE, count(*) as COUNTROW\nfrom idb_data_user.IMPL_IFMX_ACCESS_LOG where SERVER_IP = ? group by LOG_HOUR, LOG_MINUTES, LOG_SECOND, LOG_DATE \norder by LOG_DATE asc";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            countRow.add(rs.getInt("COUNTROW"));
        }
        return countRow;
    }

    public static List<String> checkingServerIPNA(Connection conn) throws SQLException {
        ArrayList<String> serverIPList = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select SERVER_IP as SERVER_IP_LIST\nfrom idb_data_user.IMPL_IFMX_ACCESS_LOG group by SERVER_IP";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            serverIPList.add(rs.getString("SERVER_IP_LIST"));
        }
        return serverIPList;
    }

    public static List<Integer> checkingLogRow(Connection conn, LocalDate lastDate, LocalTime lastTime, String serverIP) throws SQLException {
        ArrayList<Integer> countRow = new ArrayList<Integer>();
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select LOG_HOUR, LOG_MINUTES, LOG_SECOND, LOG_DATE, count(*) as COUNTROW\nfrom idb_data_user.IMPL_IFMX_ACCESS_LOG where  LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00'::time else ? end) and SERVER_IP = ?\ngroup by LOG_HOUR, LOG_MINUTES, LOG_SECOND, LOG_DATE \norder by LOG_DATE asc";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setDate(1, Date.valueOf(lastDate));
        preparedStatement.setDate(2, Date.valueOf(lastDate));
        preparedStatement.setTime(3, Time.valueOf(lastTime));
        preparedStatement.setString(4, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            countRow.add(rs.getInt("COUNTROW"));
        }
        return countRow;
    }

    public static List<String> checkingServerIP(Connection conn, String lastDate, String lastTime) throws SQLException {
        ArrayList<String> serverIPList = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select SERVER_IP as SERVER_IP_LIST\nfrom idb_data_user.IMPL_IFMX_ACCESS_LOG where  LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00'::time else ?::time end)\ngroup by SERVER_IP";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, lastDate);
        preparedStatement.setString(2, lastDate);
        preparedStatement.setString(3, lastTime);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            serverIPList.add(rs.getString("COUNTROW"));
        }
        return serverIPList;
    }

    public static void insertLogCountNA(Connection conn, LocalDate lastDate, LocalTime lastTime, String tpsTypeMonitoring, String tpsType, String serverIP) throws SQLException {
        String sql = "INSERT INTO idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING values (?, ?, ?, 0, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, tpsTypeMonitoring);
        prepstmt.setTime(2, Time.valueOf(lastTime));
        prepstmt.setDate(3, Date.valueOf(lastDate));
        prepstmt.setString(4, tpsType);
        prepstmt.setString(5, serverIP);
        prepstmt.executeUpdate();
        prepstmt.close();
        System.out.println("Data successfully added!");
        
    }

    public static void insertLogCount(Connection conn, LocalDate lastDate, LocalTime lastTime, String tpsTypeMonitoring, String tpsType, String serverIP) throws SQLException {
        String sql = "INSERT INTO idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING values (?, ?, ?, 1, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, tpsTypeMonitoring);
        prepstmt.setTime(2, Time.valueOf(lastTime));
        prepstmt.setDate(3, Date.valueOf(lastDate));
        prepstmt.setString(4, tpsType);
        prepstmt.setString(5, serverIP);
        prepstmt.executeUpdate();
        prepstmt.close();
        System.out.println("Data successfully added!");
    }
}
