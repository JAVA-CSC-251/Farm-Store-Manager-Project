package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.util.*;

public class LifetimeRepo {
    public static List<String[]> all(){
        return Csv.read(PathsCfg.p("lifetime_transactions.csv"));
    }
    public static void save(List<String[]> rows){
        Csv.write(PathsCfg.p("lifetime_transactions.csv"),
                "date,receiptId,items,price", rows);
    }
}
