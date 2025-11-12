package edu.ftcc.farmstore.model;

public class Animal {
    public String id, species, breed, sex, microchipId, supplierName, notes;
    public int ageMonths;
    public double price;
    public boolean onHold, sold;
    public Animal(String id,String species,String breed,String sex,int ageMonths,String microchipId,double price,boolean onHold,String supplierName,String notes,boolean sold){
        this.id=id; this.species=species; this.breed=breed; this.sex=sex; this.ageMonths=ageMonths; this.microchipId=microchipId;
        this.price=price; this.onHold=onHold; this.supplierName=supplierName; this.notes=notes; this.sold=sold;
    }
}
