/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author rizaac
 */
public class DBconnect {
    static String db_host = "localhost";
    static String db_port = "1433";
    static String db_name = "UDM";
    static String db_username = "STG";
    static String db_password = "P@ssw0rd";    

    private static Connection getDBConnection(String db_host, String db_port, String db_name, String db_username, String db_password) {
        Connection conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String db_url = "jdbc:jtds:sqlserver://"+db_host+":"+db_port+"/";
            conn = DriverManager.getConnection(db_url+db_name+"/",db_username,db_password);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Class not found, error: " + cnfe);
            return null;
        } catch (SQLException e){
            System.out.println(e.getCause());
            return null;
        }
        return conn;
    }
    
    public static Connection getdbconn(){
        return getDBConnection("localhost", "1433", "UDM", "STG", "P@ssw0rd");
    }
}
