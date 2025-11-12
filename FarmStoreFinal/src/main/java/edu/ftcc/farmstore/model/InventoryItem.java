package edu.ftcc.farmstore.model;

public class InventoryItem {
    public String id, sku, name, category;
    public double unitPrice;
    public int qtyOnHand;
    public boolean taxable;

    public InventoryItem(String id, String sku, String name, String category, double unitPrice, int qtyOnHand, boolean taxable){
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.qtyOnHand = qtyOnHand;
        this.taxable = taxable;
    }
}
