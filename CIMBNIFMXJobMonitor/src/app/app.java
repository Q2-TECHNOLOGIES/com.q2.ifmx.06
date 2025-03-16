/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  main.Decryptor
 */
package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import Common.Logging;
import Common.PropertiesLoader;
import ch.qos.logback.classic.LoggerContext;
import java.util.Properties;
import org.slf4j.LoggerFactory;

public class app {
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("app");
    public static PropertiesLoader pl;
    
    private static Connection getDBConnection(String db_host, String db_port, String db_name, String db_username, String db_password) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + db_host + ":" + db_port + "/" + db_name, db_username, db_password);
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Class not found, error: " + cnfe);
            System.exit(0);
        }
        catch (SQLException e) {
            System.out.println(e.getCause());
        }
        return conn;
    }

    public static void main(String[] args) throws SQLException, Exception {
        pl = new PropertiesLoader(args[0]);
        Properties prop = new Properties();
        Logging log = new Logging(); 
        log.configLog(pl,logger,loggerContext);
        String dateFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeFormat = new SimpleDateFormat("HHmmssSSS").format(new Date());
        
        block17: {
            String execution_type = pl.execution_type;
            String job_exec_id = dateFormat + timeFormat;
            String job_name = pl.job_name;
            String job_type = pl.job_type;
            String start_time = pl.start_time;
            String end_time = pl.end_time;
            String status = pl.status;
            String notes = pl.notes;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currDt = new Date();
            if ("Y".equals(start_time)) {
                start_time = sdf1.format(currDt);
            }
            if ("Y".equals(end_time)) {
                end_time = sdf1.format(currDt);
            }
            String db_host = pl.db_host;
            String db_port = pl.db_port;
            String db_name = pl.db_name;
            String db_username = pl.db_username;
            String db_password = pl.db_password;
            Connection conn = getDBConnection(db_host, db_port, db_name, db_username, db_password);
            if (conn != null) {
                try {
                    if ("I".equals(execution_type)) {
                        String query = "INSERT INTO idb_data_user.IMPL_IFMX_JOB_MONITOR VALUES ('" + job_exec_id + "','" + job_name + "','" + job_type + "','" + start_time + "',";
                        query = "NULL".equals(end_time) ? query + "NULL," : query + "'" + end_time + "',";
                        query = query + "'" + status + "','" + notes + "')";
                        Statement stmt = conn.createStatement();
                        stmt.execute(query);
                        logger.info("job monitor successfully inserted");
                        break block17;
                    }
                    if ("U".equals(execution_type)) {
                        String query = "UPDATE idb_data_user.IMPL_IFMX_JOB_MONITOR ";
                        if (!"".equals(job_exec_id)) {
                            query = query + "SET JOB_EXECUTION_ID = '" + job_exec_id + "'";
                        }
                        if (!"NULL".equals(job_name)) {
                            query = query + ", JOB_NAME = '" + job_name + "'";
                        }
                        if (!"NULL".equals(job_type)) {
                            query = query + ", JOB_TYPE = '" + job_type + "'";
                        }
                        if (!"NULL".equals(start_time)) {
                            query = query + ", START_TIME = '" + start_time + "'";
                        }
                        if (!"NULL".equals(end_time)) {
                            query = query + ", END_TIME = '" + end_time + "'";
                        }
                        if (!"NULL".equals(status)) {
                            query = query + ", STATUS = '" + status + "'";
                        }
                        if (!"NULL".equals(notes)) {
                            query = query + ", NOTES = '" + notes + "'";
                        }
                        query = query + " WHERE JOB_EXECUTION_ID = '" + job_exec_id + "'";
                        Statement stmt = conn.createStatement();
                        int updateResult = stmt.executeUpdate(query);
                        if (updateResult == 0) {
                            logger.info("0 record job monitor has been updated");
                        } else {
                            logger.info(updateResult + " record job monitor has been updated");
                        }
                        break block17;
                    }
                    logger.info("Execuion type unknown. Valid value is I (insert) and U (update)");
                }
                catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            } else {
                logger.error("Unable to connect to DB");
            }
        }
    }

    private static void format(Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
