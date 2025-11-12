package edu.ftcc.farmstore.model;

import java.time.LocalDateTime;
import java.util.*;

public class Sale {
    public String id;
    public LocalDateTime dateTime = LocalDateTime.now();
    public String customerId;
    public java.util.List<SaleLine> lines = new ArrayList<>();
    public double subTotal, tax, total, paidCash, paidCard;

    public Sale(String id){ this.id=id; }
}
