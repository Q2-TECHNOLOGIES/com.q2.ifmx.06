/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import static app.app.logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author rizaac
 */
public class dbConn {
    
    /*static String db_host = "localhost";
    static String db_port = "1433";
    static String db_name = "UDM";
    static String db_username = "STG";
    static String db_password = "P@ssw0rd";*/
    

    /*private static Connection getDBConnection(String db_host, String db_port, String db_name, String db_username, String db_password) {
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
    }*/
    private static Connection getDBConnection(String db_host, String db_port, String db_username, String db_name, String db_password) {
        Connection conn = null;
        try {
            /*Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String db_url = "jdbc:jtds:sqlserver://" + db_host + ":" + db_port + "/";
            conn = DriverManager.getConnection(db_url + db_name + "/", db_username, db_password);
            */
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + db_host + ":" + db_port + "/" + db_name, db_username, db_password);

            // Format the PostgreSQL connection string
            //String db_url = "jdbc:postgresql://" + db_host + ":" + db_port + "/" + db_name;

            // Establish the connection
            //conn = DriverManager.getConnection(db_url, db_username, db_password);
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Class not found, error: " + cnfe);
            logger.error("error while trying connect to database " + cnfe);
            return null;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            logger.error("error while trying connect to database " + e.getMessage());
            return null;
        }
        return conn;
    }
    
    public static Connection getdbconn(String db_host, String db_port, String db_username, String db_name, String db_password) {
        return dbConn.getDBConnection(db_host, db_port, db_username, db_name, db_password);
    }
}
