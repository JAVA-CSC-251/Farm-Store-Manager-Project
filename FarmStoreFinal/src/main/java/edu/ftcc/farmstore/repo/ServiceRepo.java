package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.ServiceType;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.util.*;

public class ServiceRepo {
    public static List<ServiceType> all(){
        List<ServiceType> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("services.csv"))){
            if (r.length<5) continue;
            list.add(new ServiceType(r[0], r[1], r[2], d(r[3]), i(r[4])));
        }
        return list;
    }
    public static void saveAll(List<ServiceType> items){
        List<String[]> rows = new ArrayList<>();
        for (ServiceType s: items) rows.add(new String[]{s.id,s.name,s.description,Double.toString(s.basePrice),Integer.toString(s.durationMinutes)});
        Csv.write(PathsCfg.p("services.csv"), "id,name,description,basePrice,durationMinutes", rows);
    }
    public static Optional<ServiceType> byId(String id){ return all().stream().filter(s->s.id.equals(id)).findFirst(); }

    private static int i(String s){ try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; } }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
}
