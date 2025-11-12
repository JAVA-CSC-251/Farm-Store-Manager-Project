package edu.ftcc.farmstore.model;

public class SaleLine {
    public String itemType; // ITEM|ANIMAL|SERVICE
    public String refId, description; 
    public int qty; 
    public double unitPrice; 
    public boolean taxable; 
    public double lineTotal;

    public static SaleLine item(String refId, String name, int qty, double unitPrice, boolean taxable){
        SaleLine s = new SaleLine();
        s.itemType="ITEM"; s.refId=refId; s.description=name; s.qty=qty; s.unitPrice=unitPrice; s.taxable=taxable; s.lineTotal=qty*unitPrice;
        return s;
    }
    public static SaleLine animal(String refId, String label, double price){
        SaleLine s = new SaleLine();
        s.itemType="ANIMAL"; s.refId=refId; s.description=label; s.qty=1; s.unitPrice=price; s.taxable=false; s.lineTotal=price;
        return s;
    }
}
