package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.InventoryItem;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.util.*;

public class InventoryRepo {
    public static List<InventoryItem> all(){
        List<InventoryItem> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("inventory.csv"))){
            if (r.length<7) continue;
            list.add(new InventoryItem(r[0], r[1], r[2], r[3],
                    d(r[4]), i(r[5]), b(r[6])));
        }
        return list;
    }
    public static void saveAll(List<InventoryItem> items){
        List<String[]> rows = new ArrayList<>();
        for (InventoryItem it : items){
            rows.add(new String[]{it.id,it.sku,it.name,it.category,
                    Double.toString(it.unitPrice), Integer.toString(it.qtyOnHand),
                    Boolean.toString(it.taxable)});
        }
        Csv.write(PathsCfg.p("inventory.csv"),
                "id,sku,name,category,unitPrice,qtyOnHand,taxable", rows);
    }
    public static Optional<InventoryItem> bySku(String sku){
        return all().stream().filter(i->i.sku.equalsIgnoreCase(sku)).findFirst();
    }

    private static int i(String s){ try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; } }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
    private static boolean b(String s){ return "true".equalsIgnoreCase(s.trim()); }
}
