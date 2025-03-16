/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author rizaac
 */
public class AccessLogData {
    public LocalTime Log_Time;
    public LocalDate Log_Date;
    public String Log_Content;
    public String Trx_Status_Code;
    public String Log_Hour;
    public String Log_Minutes;
    public String Log_Second;
    public String Server_IP;
    public String Transaction_Arrival_Time;
    public String Transaction_Id;
    public String Response_Time;
    public String Time_In_Flow;
    public String Status;
    public String Process_Thread_Id;
    public String Web_Server_Global_Req_Number;
    public String Web_Server_Total_Response_Time;
    public String Web_Server_Thread_Id;
    public String Web_Server_Status;

    public LocalTime getLog_Time() {
        return Log_Time;
    }

    public void setLog_Time(LocalTime Log_Time) {
        this.Log_Time = Log_Time;
    }

    public LocalDate getLog_Date() {
        return Log_Date;
    }

    public void setLog_Date(LocalDate Log_Date) {
        this.Log_Date = Log_Date;
    }

    public String getLog_Content() {
        return Log_Content;
    }

    public void setLog_Content(String Log_Content) {
        this.Log_Content = Log_Content;
    }

    public String getTrx_Status_Code() {
        return Trx_Status_Code;
    }

    public void setTrx_Status_Code(String Trx_Status_Code) {
        this.Trx_Status_Code = Trx_Status_Code;
    }

    public String getLog_Hour() {
        return Log_Hour;
    }

    public void setLog_Hour(String Log_Hour) {
        this.Log_Hour = Log_Hour;
    }

    public String getLog_Minutes() {
        return Log_Minutes;
    }

    public void setLog_Minutes(String Log_Minutes) {
        this.Log_Minutes = Log_Minutes;
    }

    public String getLog_Second() {
        return Log_Second;
    }

    public void setLog_Second(String Log_Second) {
        this.Log_Second = Log_Second;
    }

    public String getServer_IP() {
        return Server_IP;
    }

    public void setServer_IP(String Server_IP) {
        this.Server_IP = Server_IP;
    }

    public String getTransaction_Arrival_Time() {
        return Transaction_Arrival_Time;
    }

    public void setTransaction_Arrival_Time(String Transaction_Arrival_Time) {
        this.Transaction_Arrival_Time = Transaction_Arrival_Time;
    }

    public String getTransaction_Id() {
        return Transaction_Id;
    }

    public void setTransaction_Id(String Transaction_Id) {
        this.Transaction_Id = Transaction_Id;
    }

    public String getResponse_Time() {
        return Response_Time;
    }

    public void setResponse_Time(String Response_Time) {
        this.Response_Time = Response_Time;
    }

    public String getTime_In_Flow() {
        return Time_In_Flow;
    }

    public void setTime_In_Flow(String Time_In_Flow) {
        this.Time_In_Flow = Time_In_Flow;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getProcess_Thread_Id() {
        return Process_Thread_Id;
    }

    public void setProcess_Thread_Id(String Process_Thread_Id) {
        this.Process_Thread_Id = Process_Thread_Id;
    }

    public String getWeb_Server_Global_Req_Number() {
        return Web_Server_Global_Req_Number;
    }

    public void setWeb_Server_Global_Req_Number(String Web_Server_Global_Req_Number) {
        this.Web_Server_Global_Req_Number = Web_Server_Global_Req_Number;
    }

    public String getWeb_Server_Total_Response_Time() {
        return Web_Server_Total_Response_Time;
    }

    public void setWeb_Server_Total_Response_Time(String Web_Server_Total_Response_Time) {
        this.Web_Server_Total_Response_Time = Web_Server_Total_Response_Time;
    }

    public String getWeb_Server_Thread_Id() {
        return Web_Server_Thread_Id;
    }

    public void setWeb_Server_Thread_Id(String Web_Server_Thread_Id) {
        this.Web_Server_Thread_Id = Web_Server_Thread_Id;
    }

    public String getWeb_Server_Status() {
        return Web_Server_Status;
    }

    public void setWeb_Server_Status(String Web_Server_Status) {
        this.Web_Server_Status = Web_Server_Status;
    }
}
