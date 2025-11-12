package edu.ftcc.farmstore.service;

import edu.ftcc.farmstore.model.Sale;

public class PricingService {
    public static final double TAX_RATE = 0.07;

    public void computeTotals(Sale sale){
        double sub=0, tax=0;
        for (var l : sale.lines){
            sub += l.lineTotal;
            if (l.taxable) tax += l.lineTotal*TAX_RATE;
        }
        sale.subTotal=sub; sale.tax=tax; sale.total=sub+tax;
    }
}
