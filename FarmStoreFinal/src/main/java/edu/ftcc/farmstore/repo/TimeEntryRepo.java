package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.TimeEntry;
import edu.ftcc.farmstore.util.Csv;
import edu.ftcc.farmstore.util.PathsCfg;

import java.time.LocalDateTime;
import java.util.*;

public class TimeEntryRepo {
    public static List<TimeEntry> all(){
        List<TimeEntry> list = new ArrayList<>();
        for (String[] r : Csv.read(PathsCfg.p("time_entries.csv"))){
            if (r.length<6) continue;
            list.add(new TimeEntry(r[0], r[1], LocalDateTime.parse(r[2]), LocalDateTime.parse(r[3]), d(r[4]), d(r[5])));
        }
        return list;
    }
    public static void saveAll(List<TimeEntry> items){
        List<String[]> rows = new ArrayList<>();
        for (TimeEntry t: items){
            rows.add(new String[]{t.id,t.employeeId, t.clockIn.toString(), t.clockOut==null? "" : t.clockOut.toString(),
                    Double.toString(t.hours), Double.toString(t.pay)});
        }
        Csv.write(PathsCfg.p("time_entries.csv"),
                "id,employeeId,clockIn,clockOut,hours,pay", rows);
    }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
}
