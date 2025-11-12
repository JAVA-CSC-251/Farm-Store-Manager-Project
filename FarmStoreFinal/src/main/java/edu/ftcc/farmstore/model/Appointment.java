package edu.ftcc.farmstore.model;

import java.time.LocalDateTime;

public class Appointment {
    public String id, customerId, animalId, serviceId, status;
    public LocalDateTime start, end;
    public double paidAmount;
    public Appointment(String id,String customerId,String animalId,String serviceId,LocalDateTime start,LocalDateTime end,String status,double paidAmount){
        this.id=id; this.customerId=customerId; this.animalId=animalId; this.serviceId=serviceId; this.start=start; this.end=end; this.status=status; this.paidAmount=paidAmount;
    }
}
