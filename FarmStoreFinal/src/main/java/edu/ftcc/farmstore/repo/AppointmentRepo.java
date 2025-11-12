package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.Appointment;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.time.LocalDateTime;
import java.util.*;

public class AppointmentRepo {
    public static List<Appointment> all(){
        List<Appointment> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("appointments.csv"))){
            if (r.length<8) continue;
            list.add(new Appointment(r[0], r[1], n(r[2]), r[3],
                    LocalDateTime.parse(r[4]), LocalDateTime.parse(r[5]), r[6], d(r[7])));
        }
        return list;
    }
    public static void saveAll(List<Appointment> items){
        List<String[]> rows = new ArrayList<>();
        for (Appointment a: items){
            rows.add(new String[]{a.id,a.customerId,nn(a.animalId),a.serviceId,
                    a.start.toString(), a.end.toString(), a.status, Double.toString(a.paidAmount)});
        }
        Csv.write(PathsCfg.p("appointments.csv"),
                "id,customerId,animalId,serviceId,start,end,status,paidAmount", rows);
    }

    private static String n(String s){ return (s==null || s.isBlank()) ? null : s; }
    private static String nn(String s){ return s==null? "" : s; }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
}
