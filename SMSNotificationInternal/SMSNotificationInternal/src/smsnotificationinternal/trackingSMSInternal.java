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
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 *
 * @author FS40049X
 */
public class trackingSMSInternal {
    public static Integer checkingCountTrackingTable(Connection conn, String partyKey, String ruleName) throws SQLException{
        int result = 0;
        ResultSet rs;
        PreparedStatement prepstmt = null;
        String strQueryLogCount = "select count(*) as COUNT_ROW from IDB.dbo.TRACKING_SMS_INTERNAL where PARTY_ID = ? and RULE_NAME = ? and CONVERT(DATE, PROCESSED_DATE) = ?" ; 
        prepstmt = conn.prepareStatement(strQueryLogCount);
        prepstmt.setString(1, partyKey);
        prepstmt.setString(2, ruleName);
        prepstmt.setDate  (3, java.sql.Date.valueOf(LocalDate.now()));
        rs = prepstmt.executeQuery();
        
        while(rs.next()) {
            result = rs.getInt("COUNT_ROW");
        }
        return result;
    }
    
    
    public static void insertToTrackingTable(Connection conn, String partyKey, String ruleName, String message) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO IDB.dbo.TRACKING_SMS_INTERNAL values (?, ?, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, partyKey);
        prepstmt.setString(2, ruleName);
        prepstmt.setString(3, message);
        prepstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully insert into tracking table");
    }
}
