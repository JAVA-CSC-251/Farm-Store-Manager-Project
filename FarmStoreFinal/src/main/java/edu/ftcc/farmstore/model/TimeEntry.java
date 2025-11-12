package edu.ftcc.farmstore.model;

import java.time.LocalDateTime;

public class TimeEntry {
    public String id, employeeId;
    public LocalDateTime clockIn, clockOut;
    public double hours, pay;
    public TimeEntry(String id, String employeeId, LocalDateTime in, LocalDateTime out, double hours, double pay){
        this.id=id; this.employeeId=employeeId; this.clockIn=in; this.clockOut=out; this.hours=hours; this.pay=pay;
    }
}
