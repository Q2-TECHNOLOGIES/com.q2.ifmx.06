/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author rizaac
 */
public class main {

    /**
     * @param args the command line arguments
     */
    
    public static String insertToDB(Connection conn, String time, LocalDate date, String data, String status, 
            String hour, String minute, String second, String serverIP) throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO dbo.IMPL_IFMX_ACCESS_LOG VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);

        prepstmt.setString(1, time);
        prepstmt.setDate(2, java.sql.Date.valueOf(date));
        prepstmt.setString(3, data);
        prepstmt.setString(4, status);
        prepstmt.setString(5, hour);
        prepstmt.setString(6, minute);
        prepstmt.setString(7, second);
        prepstmt.setString(8, serverIP);
        prepstmt.executeUpdate();
        prepstmt.close();
        
        return "Data successfully added!";
    }
    
    private static void sleepMinutes(int minutes) {
        try {
            System.out.println("Sleeping for " + minutes + " minutes...");
            Thread.sleep(minutes * 1000 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        String filePath = args[0];
        String serverIP = args[1];
        
        /*List<String> filesInFolder = Files.walk(Paths.get(filePath))
                                .filter(Files::isRegularFile)
                                .map(Path::toString)
                                .collect(Collectors.toList());*/
        int finalMinutes = Integer.parseInt(args[2]);
        
        while (true) {
            SimpleDateFormat bartDateFormat = new SimpleDateFormat("mm");
            Date date = new Date();
            int currentMin = new Integer(bartDateFormat.format(date)).intValue();
            List<String> filesInFolder = new ArrayList<>(); 
        
            try {
                File folder = new File(filePath);

                File[] files = folder.listFiles();
                for(File file : files) {
                    if(file.isFile()) {
                        if(file.toString().contains("Custom.")){
                            filesInFolder.add(file.toString());
                        }
                      }
                   }
            } catch (Exception e) {
                e.getStackTrace();
            }
                //read each files    
            for(String files : filesInFolder){
                String regexPattern = "^(.{1})(\\d{4}-.{3}-\\d{2})\\s((\\d{2}):(\\d{2}):(\\d{2})\\.\\d{3})\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)";
                String regexPattern2 = "^(\\d{4}-.{3}-\\d{2})\\s((\\d{2}):(\\d{2}):(\\d{2})\\.\\d{3})\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)";

                final Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL | Pattern.MULTILINE);
                final Pattern pattern2 = Pattern.compile(regexPattern2, Pattern.DOTALL | Pattern.MULTILINE);

                System.out.println("-------------" + files + "-------------");
                List<String> result = new ArrayList<>();

                try(BufferedReader br = Files.newBufferedReader(Paths.get(files))){
                    String line;
                    while((line = br.readLine()) != null){
                        result.add(line);
                    }
                }catch(IOException e){
                    System.out.println(e.getCause());
                }
                
                System.out.println("----------------------------------");

                for(int i=0;i<result.size();i++){
                    String data = result.get(i);
                    //System.out.println(data);
                    Matcher messages = pattern.matcher(data);
                    Matcher messages2 = pattern2.matcher(data);

                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
                    if(messages.matches()){
                        //System.out.println(data);
                        //messages2.matches();
                        //System.out.println(messages.groupCount());
                        System.out.println(messages.group(0)); //all data
                        System.out.println("-1-  "+ messages.group(1)); 
                        System.out.println("-2-  "+ messages.group(2)); // date
                        System.out.println("-3-  "+ messages.group(3)); //time
                        System.out.println("-4-  "+ messages.group(4)); // hour
                        System.out.println("-5-  "+ messages.group(5)); //minute
                        System.out.println("-6-  "+ messages.group(6));// second
                        System.out.println("-7-  "+ messages.group(7));
                        System.out.println("-8-  "+ messages.group(8));
                        System.out.println("-9-  "+ messages.group(9));
                        System.out.println("-10-  "+ messages.group(10));
                        System.out.println("-11-  "+ messages.group(11));
                        System.out.println("-12-  "+ messages.group(12));
                        System.out.println("-13-  "+ messages.group(13));
                        System.out.println("-14-  "+ messages.group(14)); 
                        System.out.println("-15-  "+ messages.group(15));//status

                        LocalDate dbDate = LocalDate.parse(messages.group(2), df);

                        Connection conn = DBconnect.getdbconn();
                        try {
                            String dbInputStatus = insertToDB(conn, messages.group(3), dbDate, messages.group(0).substring(1), 
                                    messages.group(15), messages.group(4), messages.group(5), 
                                    messages.group(6), serverIP);
                            System.out.println(dbInputStatus);
                            conn.close();
                            System.out.println("----------------------------------");
                        } catch (Exception e) {
                            System.out.println("err" + e.getMessage());
                        }

                    }else if(messages2.matches()){
                            //System.out.println(data);
                        //messages2.matches();
                        //System.out.println(messages.groupCount());
                        System.out.println(messages2.group(0)); //all data
                        System.out.println("-1-  "+ messages2.group(1)); // date
                        System.out.println("-2-  "+ messages2.group(2)); //time
                        System.out.println("-3-  "+ messages2.group(3)); // hour
                        System.out.println("-4-  "+ messages2.group(4)); //minute
                        System.out.println("-5-  "+ messages2.group(5)); // second
                        System.out.println("-6-  "+ messages2.group(6));
                        System.out.println("-7-  "+ messages2.group(7));
                        System.out.println("-8-  "+ messages2.group(8));
                        System.out.println("-9-  "+ messages2.group(9));
                        System.out.println("-10-  "+ messages2.group(10));
                        System.out.println("-11-  "+ messages2.group(11));
                        System.out.println("-12-  "+ messages2.group(12));
                        System.out.println("-13-  "+ messages2.group(13));
                        System.out.println("-14-  "+ messages2.group(14)); //status
                        
                        LocalDate dbDate = LocalDate.parse(messages2.group(1), df);

                        Connection conn = DBconnect.getdbconn();
                        try {
                            String dbInputStatus = insertToDB(conn, messages2.group(2), dbDate, messages2.group(0), 
                                    messages2.group(14), messages2.group(3), messages2.group(4), 
                                    messages2.group(5), serverIP);
                            System.out.println(dbInputStatus);
                            conn.close();
                            System.out.println("----------------------------------");

                        } catch (Exception e) {
                            System.out.println("err" + e.getMessage());
                        }
                    }else{
                        System.out.println(data);
                        System.out.println("out of condition");
                        System.out.println("----------------------------------");
                    }
                    }
                }
                if (currentMin > finalMinutes){
                    sleepMinutes(Math.abs(finalMinutes - (currentMin % finalMinutes)));
                }else{
                    sleepMinutes(finalMinutes - currentMin);
                }
        }
}
}