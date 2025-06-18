/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailnotification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author FS40049X
 */
public class sendStatus {
    
    public static void updateSendStatusMessageNull(Connection conn, String trxKey, String trxMapping, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_EMAIL_ALERT_NOTIFICATION set SEND_STATUS = 2 where TRX_KEY = ? and TRX_MAPPING = ?  and ACT_TRX_KEY = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, trxMapping);
        prepstmt.setString(3, actTrxKey);
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully updated send status");
    }
    
    public static void updateSendStatus(Connection conn, String trxKey, String trxMapping, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_EMAIL_ALERT_NOTIFICATION set SEND_STATUS = 1 where TRX_KEY = ? and TRX_MAPPING = ?  and ACT_TRX_KEY = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, trxMapping);
        prepstmt.setString(3, actTrxKey);
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully updated send status");
    }
    public static void updateSendStatusMessageError(Connection conn, String trxKey, String trxMapping, String actTrxKey) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "update IDB.dbo.IMPL_EMAIL_ALERT_NOTIFICATION set SEND_STATUS = 4 where TRX_KEY = ? and TRX_MAPPING = ?  and ACT_TRX_KEY = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, trxKey);
        prepstmt.setString(2, trxMapping);
        prepstmt.setString(3, actTrxKey);
        prepstmt.executeUpdate();
        prepstmt.close();
        
        System.out.println("Successfully updated send status");
    }
}
