package edu.ftcc.farmstore.model;

public class Employee {
    public String id, name;
    public double hourlyRate;
    public boolean active;
    public Employee(String id, String name, double hourlyRate, boolean active){
        this.id=id; this.name=name; this.hourlyRate=hourlyRate; this.active=active;
    }
}
