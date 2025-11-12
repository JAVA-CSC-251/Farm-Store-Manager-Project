package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.Animal;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.util.*;

public class AnimalRepo {
    public static List<Animal> all(){
        List<Animal> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("animals.csv"))){
            if (r.length<11) continue;
            list.add(new Animal(r[0],r[1],r[2],r[3], i(r[4]), r[5], d(r[6]), b(r[7]), r[8], r[9], b(r[10])));
        }
        return list;
    }
    public static void saveAll(List<Animal> items){
        List<String[]> rows = new ArrayList<>();
        for (Animal a: items){
            rows.add(new String[]{a.id,a.species,a.breed,a.sex,
                    Integer.toString(a.ageMonths),a.microchipId,Double.toString(a.price),
                    Boolean.toString(a.onHold),a.supplierName,a.notes,Boolean.toString(a.sold)});
        }
        Csv.write(PathsCfg.p("animals.csv"),
                "id,species,breed,sex,ageMonths,microchipId,price,onHold,supplierName,notes,sold", rows);
    }
    private static int i(String s){ try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; } }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
    private static boolean b(String s){ return "true".equalsIgnoreCase(s.trim()); }
}
