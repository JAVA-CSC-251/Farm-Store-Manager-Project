package edu.ftcc.farmstore.model;

public class ServiceType {
    public String id, name, description;
    public double basePrice;
    public int durationMinutes;
    public ServiceType(String id, String name, String description, double basePrice, int durationMinutes){
        this.id=id; this.name=name; this.description=description; this.basePrice=basePrice; this.durationMinutes=durationMinutes;
    }
}
