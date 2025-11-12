package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.Customer;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.util.*;

public class CustomerRepo {
    public static List<Customer> all(){
        List<Customer> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("customers.csv"))){
            if (r.length<4) continue;
            list.add(new Customer(r[0], r[1], r[2], r[3]));
        }
        return list;
    }
    public static void saveAll(List<Customer> items){
        List<String[]> rows = new ArrayList<>();
        for (Customer c: items) rows.add(new String[]{c.id,c.fullName,c.phone,c.email});
        Csv.write(PathsCfg.p("customers.csv"),
                "id,fullName,phone,email", rows);
    }
    public static Optional<Customer> byId(String id){ return all().stream().filter(c->c.id.equals(id)).findFirst(); }
}
