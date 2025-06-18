/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsnotificationinternal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rizaac
 */
public class dbConn {
    final static Logger logger = LogManager.getLogger(app.class);
    
    private static Connection getDBConnection(String db_host, String db_port,  String db_username, String db_name, String db_password) {
        Connection conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String db_url = "jdbc:jtds:sqlserver://"+db_host+":"+db_port+"/";
            conn = DriverManager.getConnection(db_url+db_name+"/",db_username,db_password);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Class not found, error: " + cnfe);
            logger.error("error while trying connect to database " + cnfe);
            return null;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            logger.error("error while trying connect to database " + e.getMessage());
            return null;
        }
        return conn;
    }
    
    public static Connection getdbconn(String db_host, String db_port, String db_username, String db_name, String db_password){
        //return getDBConnection("localhost", "1433", "UDM", "STG", "P@ssw0rd");
        return getDBConnection(db_host, db_port, db_username, db_name, db_password);
    }
}
