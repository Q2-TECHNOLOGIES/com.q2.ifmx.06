/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsnotificationinternal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author FS40049X
 */
public class sendStatus {
    
        public static void updateSendStatus(Connection conn, String trxKey, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_SMS_ALERT_NOTIFICATION\n" +
                    "set SEND_STATUS = 1 where TRX_KEY = ? and ACT_TRX_KEY = ? and SMS_SEND_TYPE = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, actTrxKey);
        prepstmt.setString(3, "I");
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully update send status");
    }
    
    public static void updateSendStatusMaximum(Connection conn, String trxKey, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_SMS_ALERT_NOTIFICATION\n" +
                    "set SEND_STATUS = 2 where TRX_KEY = ? and ACT_TRX_KEY = ? and SMS_SEND_TYPE = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, actTrxKey);
        prepstmt.setString(3, "I");
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully update send status");
    }
    
    public static void updateSendStatusTemplate(Connection conn, String trxKey, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_SMS_ALERT_NOTIFICATION\n" +
                    "set SEND_STATUS = 3 where TRX_KEY = ? and ACT_TRX_KEY = ? and SMS_SEND_TYPE = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, actTrxKey);
        prepstmt.setString(3, "I");
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully update send status");
    }
    
    public static void updateSendStatusError(Connection conn, String trxKey, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_SMS_ALERT_NOTIFICATION\n" +
                    "set SEND_STATUS = 4 where TRX_KEY = ? and ACT_TRX_KEY = ? and SMS_SEND_TYPE = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, actTrxKey);
        prepstmt.setString(3, "I");
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully update send status");
    }
}
