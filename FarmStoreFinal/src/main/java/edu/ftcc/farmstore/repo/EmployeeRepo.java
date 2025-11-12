package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.Employee;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.util.*;

public class EmployeeRepo {
    public static List<Employee> all(){
        List<Employee> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("employees.csv"))){
            if (r.length<4) continue;
            list.add(new Employee(r[0], r[1], d(r[2]), b(r[3])));
        }
        return list;
    }
    public static void saveAll(List<Employee> items){
        List<String[]> rows = new ArrayList<>();
        for (Employee e: items){
            rows.add(new String[]{e.id,e.name, Double.toString(e.hourlyRate), Boolean.toString(e.active)});
        }
        Csv.write(PathsCfg.p("employees.csv"),
                "id,name,hourlyRate,active", rows);
    }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
    private static boolean b(String s){ return "true".equalsIgnoreCase(s.trim()); }
}
