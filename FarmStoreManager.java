import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// ✅ Use only the AWT classes we need (avoids clash with java.util.List)
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.io.*;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.Duration;   // Haylee
import java.time.LocalDateTime;
import java.util.*;

/**
 * FarmStoreManager — single-file Java app (Swing)
 * Creates ./data folder + seeded CSVs on first run. No manual files needed.
 */
public class FarmStoreManager extends JFrame {

    public static void main(String[] args) {
        // === Global safety net: any uncaught exception shows a dialog and gets logged ===
        // Haylee - Added global exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
            Err.alert(null, "running the application", e)
        );

        SwingUtilities.invokeLater(() -> {
            FarmStoreManager app = new FarmStoreManager();
            app.setVisible(true);
        });
    }

    // ----------- App Frame -----------
    public FarmStoreManager() {
        super("Farm Store Manager — Single File");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);

        // Ensure data dir and headers + seed rows exist
        CsvFiles.ensureAllWithSeed();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Store", new StorePanel());
        tabs.addTab("Services", new ServicesPanel());
        tabs.addTab("Animals", new AnimalsPanel());
        // Haylee - Added Employees tabs
        tabs.addTab("Employees", new EmployeesPanel());
        tabs.addTab("Reports", new ReportsPanel());
        add(tabs, BorderLayout.CENTER);
    }

    // ===================== Data & Utils =====================

    static final String DATA_DIR = "data";  // one folder, auto-created
    static final double TAX_RATE = 0.07;
    static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();

    static String money(double d) { return CURRENCY.format(d); }
    static String id(String prefix) { return prefix + "-" + UUID.randomUUID().toString().substring(0,8); }

    // Haylee - Yes/No helper
    private static String yn(boolean b) { return b ? "Yes" : "No"; }

    // ===================== Error Handling Helpers =====================
    // Haylee - Centralized error handling
    static final class Err {
        private static Path logPath() {
            try { Files.createDirectories(Path.of(DATA_DIR)); } catch (IOException ignored) {}
            return Path.of(DATA_DIR, "error.log");
        }

        static void log(Throwable t) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(logPath().toFile(), true))) {
                pw.println("=== " + LocalDateTime.now() + " ===");
                t.printStackTrace(pw);
                pw.println();
            } catch (IOException ioe) {
                // last resort
                ioe.printStackTrace();
            }
        }

        static void alert(java.awt.Component parent, String doing, Throwable t) {
            log(t);
            JOptionPane.showMessageDialog(parent,
                "Sorry, something went wrong while " + doing + ".\\nDetails were saved to data/error.log",
                "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        }

        static Runnable safe(java.awt.Component parent, String doing, Runnable run) {
            return () -> {
                try { run.run(); }
                catch (Throwable t) { alert(parent, doing, t); }
            };
        }
    }

    static class CsvFiles {
        static Path p(String name){ return Path.of(DATA_DIR, name); }

        static void ensureAllWithSeed() {
            try { Files.createDirectories(Path.of(DATA_DIR)); } catch (IOException ignored) {}

            ensureWithSeed(
                p("inventory.csv"),
                "id,sku,name,category,unitPrice,qtyOnHand,taxable",
                List.of(
                    new String[]{"I1","DOGFOOD-20","Dog Kibble 20lb","Food","29.99","12","true"},
                    new String[]{"I2","CATTOY-FEATHER","Feather Toy","Toys","6.49","25","true"}
                )
            );

            ensureWithSeed(
                p("animals.csv"),
                "id,species,breed,sex,ageMonths,microchipId,price,onHold,supplierName,notes,sold",
                List.of(
                    new String[]{"A1","Chicken","Silkie","F","6","","15.00","false","Local Breeder","Healthy","false"},
                    new String[]{"A2","Rabbit","Mini Rex","M","4","","45.00","false","Local Breeder","Calm","false"}
                )
            );

            ensureWithSeed(
                p("customers.csv"),
                "id,fullName,phone,email",
                List.of(
                    new String[]{"C1","Jane Doe","910-555-0001","jane@example.com"},
                    new String[]{"C2","Bob Smith","910-555-0002","bob@example.com"}
                )
            );

            ensureWithSeed(
                p("services.csv"),
                "id,name,description,basePrice,durationMinutes",
                List.of(
                    new String[]{"S1","Nail Trim","Basic nail trim for small animals","12.00","15"},
                    new String[]{"S2","Wellness Check","General check-up","35.00","30"}
                )
            );

            ensureHeaderOnly(p("appointments.csv"), "id,customerId,animalId,serviceId,start,end,status,paidAmount");
            ensureHeaderOnly(p("sales.csv"), "id,dateTime,customerId,subTotal,tax,total,paidCash,paidCard,linesJson");

            // Haylee - Employees and Timeclock CSVs
            ensureWithSeed(
                p("employees.csv"),
                "id,name,hourlyRate,active",
                List.of(
                    new String[]{"E1","Alice Johnson","15.50","true"},
                    new String[]{"E2","Marco Diaz","14.00","true"}
                )
            );
            ensureHeaderOnly(p("timeclock.csv"), "id,employeeId,clockIn,clockOut,hours,pay");
        }

        static void ensureWithSeed(Path path, String header, List<String[]> seedRows) {
            if (!Files.exists(path) || isEmptyData(path)) {
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                    pw.println(header);
                    for (String[] r : seedRows) pw.println(String.join(",", safe(r)));
                // Haylee - Added catch block
                } catch (IOException e) {
                    Err.log(e);
                }
            }
        }

        static void ensureHeaderOnly(Path path, String header) {
            if (!Files.exists(path) || isEmptyData(path)) {
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                    pw.println(header);
                // Haylee - Added catch block
                } catch (IOException e) {
                    Err.log(e);
                }
            }
        }

        static boolean isEmptyData(Path path) {
            try {
                long lines = Files.lines(path).count();
                return lines <= 1; // header only or brand new
            // Haylee - Added catch block
            } catch (IOException e) {
                Err.log(e);
                return true;
            }
        }

        static List<String[]> read(Path path) {
            List<String[]> rows = new ArrayList<>();
            if (!Files.exists(path)) return rows;
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String line; boolean header = true;
                while ((line = br.readLine()) != null) {
                    if (header) { header = false; continue; }
                    if (line.isBlank()) continue;
                    rows.add(splitCsv(line));
                }
            // Haylee - Added catch block
            } catch (IOException e) {
                Err.log(e);
            }
            return rows;
        }

        static void write(Path path, String header, List<String[]> rows) {
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                pw.println(header);
                for (String[] r : rows) pw.println(String.join(",", safe(r)));
            // Haylee - Added catch block
            } catch (IOException e) {
                Err.log(e);
            }
        }

        // CSV helpers: simple split that supports quoted fields (basic)
        static String[] splitCsv(String line){
            List<String> out = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            boolean inQ = false;
            for (int i=0;i<line.length();i++){
                char c = line.charAt(i);
                if (c=='"') { inQ = !inQ; continue; }
                if (c==',' && !inQ){ out.add(sb.toString()); sb.setLength(0); }
                else sb.append(c);
            }
            out.add(sb.toString());
            return out.toArray(new String[0]);
        }
        static String sq(String s){ if (s==null) return ""; s = s.replace("\"","").replace("\n"," "); if (s.contains(",")) return "\""+s+"\""; return s; }
        static String[] safe(String[] arr){ String[] r = new String[arr.length]; for(int i=0;i<arr.length;i++) r[i]=sq(arr[i]); return r; }
    }

    // ===================== Models =====================

    static class InventoryItem {
        String id, sku, name, category;
        double unitPrice; int qtyOnHand; boolean taxable;
        InventoryItem(String id,String sku,String name,String category,double unitPrice,int qtyOnHand,boolean taxable){
            this.id = id==null? id("I"): id; this.sku=sku; this.name=name; this.category=category;
            this.unitPrice=unitPrice; this.qtyOnHand=qtyOnHand; this.taxable=taxable;
        }
    }

    static class Animal {
        String id,species,breed,sex;
        int ageMonths; String microchipId; double price; boolean onHold; String supplierName; String notes; boolean sold;
        Animal(String id,String species,String breed,String sex,int ageMonths,String microchipId,double price,boolean onHold,String supplierName,String notes, boolean sold){
            this.id = id==null? id("A"): id; this.species=species; this.breed=breed; this.sex=sex; this.ageMonths=ageMonths;
            this.microchipId=microchipId; this.price=price; this.onHold=onHold; this.supplierName=supplierName; this.notes=notes; this.sold=sold;
        }
    }

    static class Customer {
        String id, fullName, phone, email;
        Customer(String id,String fullName,String phone,String email){
            this.id = id==null? id("C"): id; this.fullName=fullName; this.phone=phone; this.email=email;
        }
    }

    static class Service {
        String id,name,description; double basePrice; int durationMinutes;
        Service(String id,String name,String description,double basePrice,int durationMinutes){
            this.id = id==null? id("S"): id; this.name=name; this.description=description; this.basePrice=basePrice; this.durationMinutes=durationMinutes;
        }
    }

    static class Appointment {
        String id, customerId, animalId, serviceId;
        LocalDateTime start, end; String status; double paidAmount;
        Appointment(String id,String customerId,String animalId,String serviceId,LocalDateTime start,LocalDateTime end,String status,double paidAmount){
            this.id = id==null? id("AP"): id; this.customerId=customerId; this.animalId=animalId; this.serviceId=serviceId;
            this.start=start; this.end=end; this.status=status; this.paidAmount=paidAmount;
        }
    }

    static class Sale {
        String id = id("R");
        LocalDateTime dateTime = LocalDateTime.now();
        String customerId; // optional
        List<SaleLine> lines = new ArrayList<>();
        double subTotal, tax, total, paidCash, paidCard;
    }
    static class SaleLine {
        String itemType; // ITEM|ANIMAL|SERVICE
        String refId, description; int qty; double unitPrice; boolean taxable; double lineTotal;
        static SaleLine item(InventoryItem it, int qty){
            SaleLine s = new SaleLine();
            s.itemType="ITEM"; s.refId=it.id; s.description=it.name; s.qty=qty; s.unitPrice=it.unitPrice; s.taxable=it.taxable; s.lineTotal=qty*it.unitPrice;
            return s;
        }
        static SaleLine animal(Animal a){
            SaleLine s = new SaleLine();
            s.itemType="ANIMAL"; s.refId=a.id; s.description=a.species+" ("+a.breed+")"; s.qty=1; s.unitPrice=a.price; s.taxable=false; s.lineTotal=a.price;
            return s;
        }
    }

    // ===================== Employee and TimeEntry Models =====================
    // Haylee - Employee and TimeEntry classes
    static class Employee {
        String id, name;
        double hourlyRate;
        boolean active;
        Employee(String id, String name, double hourlyRate, boolean active) {
            this.id = id == null ? id("E") : id;
            this.name = name;
            this.hourlyRate = hourlyRate;
            this.active = active;
        }
    }
    static class TimeEntry {
        String id, employeeId;
        LocalDateTime clockIn, clockOut;
        double hours, pay;
        TimeEntry(String id, String employeeId, LocalDateTime in, LocalDateTime out, double hours, double pay) {
            this.id = id == null ? id("TC") : id;
            this.employeeId = employeeId;
            this.clockIn = in;
            this.clockOut = out;
            this.hours = hours;
            this.pay = pay;
        }
    }

    // ===================== Storage (CSV-backed) =====================

    static class InventoryRepo {
        static List<InventoryItem> all() {
            List<InventoryItem> list = new ArrayList<>();
            for (String[] r : CsvFiles.read(CsvFiles.p("inventory.csv"))) {
                if (r.length<7) continue;
                list.add(new InventoryItem(r[0], r[1], r[2], r[3], d(r[4]), i(r[5]), b(r[6])));
            }
            return list;
        }
        static void saveAll(List<InventoryItem> items){
            List<String[]> rows = new ArrayList<>();
            for (InventoryItem it : items){
                rows.add(new String[]{it.id,it.sku,it.name,it.category, Double.toString(it.unitPrice), Integer.toString(it.qtyOnHand), Boolean.toString(it.taxable)});
            }
            CsvFiles.write(CsvFiles.p("inventory.csv"), "id,sku,name,category,unitPrice,qtyOnHand,taxable", rows);
        }
        static Optional<InventoryItem> bySku(String sku){
            return all().stream().filter(i->i.sku.equalsIgnoreCase(sku)).findFirst();
        }
    }

    static class AnimalRepo {
        static List<Animal> all(){
            List<Animal> list = new ArrayList<>();
            for (String[] r: CsvFiles.read(CsvFiles.p("animals.csv"))){
                if (r.length<11) continue;
                list.add(new Animal(r[0],r[1],r[2],r[3], i(r[4]), r[5], d(r[6]), b(r[7]), r[8], r[9], b(r[10])));
            }
            return list;
        }
        static void saveAll(List<Animal> items){
            List<String[]> rows = new ArrayList<>();
            for (Animal a: items){
                rows.add(new String[]{a.id,a.species,a.breed,a.sex,Integer.toString(a.ageMonths),a.microchipId,Double.toString(a.price),Boolean.toString(a.onHold),a.supplierName,a.notes,Boolean.toString(a.sold)});
            }
            CsvFiles.write(CsvFiles.p("animals.csv"), "id,species,breed,sex,ageMonths,microchipId,price,onHold,supplierName,notes,sold", rows);
        }
    }

    static class CustomerRepo {
        static List<Customer> all(){
            List<Customer> list=new ArrayList<>();
            for (String[] r: CsvFiles.read(CsvFiles.p("customers.csv"))){
                if (r.length<4) continue;
                list.add(new Customer(r[0], r[1], r[2], r[3]));
            }
            return list;
        }
        static void saveAll(List<Customer> items){
            List<String[]> rows = new ArrayList<>();
            for (Customer c: items) rows.add(new String[]{c.id,c.fullName,c.phone,c.email});
            CsvFiles.write(CsvFiles.p("customers.csv"), "id,fullName,phone,email", rows);
        }
        static Optional<Customer> byId(String id){ return all().stream().filter(c->c.id.equals(id)).findFirst(); }
    }

    static class ServiceRepo {
        static List<Service> all(){
            List<Service> list=new ArrayList<>();
            for (String[] r: CsvFiles.read(CsvFiles.p("services.csv"))){
                if (r.length<5) continue;
                list.add(new Service(r[0], r[1], r[2], d(r[3]), i(r[4])));
            }
            return list;
        }
        static void saveAll(List<Service> items){
            List<String[]> rows = new ArrayList<>();
            for (Service s: items) rows.add(new String[]{s.id,s.name,s.description,Double.toString(s.basePrice),Integer.toString(s.durationMinutes)});
            CsvFiles.write(CsvFiles.p("services.csv"), "id,name,description,basePrice,durationMinutes", rows);
        }
        static Optional<Service> byId(String id){ return all().stream().filter(s->s.id.equals(id)).findFirst(); }
    }

    static class AppointmentRepo {
        static List<Appointment> all(){
            List<Appointment> list=new ArrayList<>();
            for (String[] r: CsvFiles.read(CsvFiles.p("appointments.csv"))){
                if (r.length<8) continue;
                list.add(new Appointment(r[0], r[1], n(r[2]), r[3],
                        LocalDateTime.parse(r[4]), LocalDateTime.parse(r[5]), r[6], d(r[7])));
            }
            return list;
        }
        static void saveAll(List<Appointment> items){
            List<String[]> rows = new ArrayList<>();
            for (Appointment a: items){
                rows.add(new String[]{a.id,a.customerId,nn(a.animalId),a.serviceId,a.start.toString(),a.end.toString(),a.status,Double.toString(a.paidAmount)});
            }
            CsvFiles.write(CsvFiles.p("appointments.csv"), "id,customerId,animalId,serviceId,start,end,status,paidAmount", rows);
        }
    }

    static class SaleRepo {
        static List<Sale> all(){
            List<Sale> list=new ArrayList<>();
            for (String[] r: CsvFiles.read(CsvFiles.p("sales.csv"))){
                if (r.length<9) continue;
                Sale s = new Sale();
                s.id = r[0]; s.dateTime = LocalDateTime.parse(r[1]); s.customerId = n(r[2]);
                s.subTotal=d(r[3]); s.tax=d(r[4]); s.total=d(r[5]); s.paidCash=d(r[6]); s.paidCard=d(r[7]);
                s.lines = linesFromJson(r[8]);
                list.add(s);
            }
            return list;
        }
        static void saveAll(List<Sale> items){
            List<String[]> rows = new ArrayList<>();
            for (Sale s: items){
                rows.add(new String[]{s.id,s.dateTime.toString(),nn(s.customerId),
                        Double.toString(s.subTotal),Double.toString(s.tax),Double.toString(s.total),
                        Double.toString(s.paidCash),Double.toString(s.paidCard), linesToJson(s.lines)});
            }
            CsvFiles.write(CsvFiles.p("sales.csv"), "id,dateTime,customerId,subTotal,tax,total,paidCash,paidCard,linesJson", rows);
        }
        // tiny pseudo-JSON for lines (itemType|refId|description|qty|unitPrice|taxable|lineTotal;...)
        static String linesToJson(List<SaleLine> ls){
            StringBuilder sb=new StringBuilder();
            for (int i=0;i<ls.size();i++){
                SaleLine l = ls.get(i);
                sb.append(escape(l.itemType)).append("|").append(escape(l.refId)).append("|").append(escape(l.description)).append("|")
                        .append(l.qty).append("|").append(l.unitPrice).append("|").append(l.taxable).append("|").append(l.lineTotal);
                if (i<ls.size()-1) sb.append(";");
            }
            return sb.toString();
        }
        static List<SaleLine> linesFromJson(String s){
            List<SaleLine> out = new ArrayList<>();
            if (s==null || s.isBlank()) return out;
            for (String part: s.split(";")){
                String[] f = part.split("\\|", -1);
                if (f.length<7) continue;
                SaleLine l = new SaleLine();
                l.itemType=f[0]; l.refId=f[1]; l.description=f[2];
                l.qty=i(f[3]); l.unitPrice=d(f[4]); l.taxable=Boolean.parseBoolean(f[5]); l.lineTotal=d(f[6]);
                out.add(l);
            }
            return out;
        }
        static String escape(String s){ if (s==null) return ""; return s.replace("|","/").replace(";","/"); }
    }

    // ===================== Employee and TimeEntry Repos =====================
    // Haylee - EmployeeRepo and TimeRepo classes
    static class EmployeeRepo {
        static List<Employee> all() {
            List<Employee> list = new ArrayList<>();
            for (String[] r : CsvFiles.read(CsvFiles.p("employees.csv"))) {
                if (r.length < 4) continue;
                list.add(new Employee(r[0], r[1], d(r[2]), b(r[3])));
            }
            return list;
        }
        static void saveAll(List<Employee> items){
            List<String[]> rows = new ArrayList<>();
            for (Employee e : items){
                rows.add(new String[]{e.id, e.name, Double.toString(e.hourlyRate), Boolean.toString(e.active)});
            }
            CsvFiles.write(CsvFiles.p("employees.csv"), "id,name,hourlyRate,active", rows);
        }
        static Optional<Employee> byId(String id){
            return all().stream().filter(e->e.id.equals(id)).findFirst();
        }
    }

    static class TimeRepo {
        static List<TimeEntry> all() {
            List<TimeEntry> list = new ArrayList<>();
            for (String[] r : CsvFiles.read(CsvFiles.p("timeclock.csv"))) {
                if (r.length < 6) continue;
                LocalDateTime cin = r[2].isBlank() ? null : LocalDateTime.parse(r[2]);
                LocalDateTime cout = r[3].isBlank() ? null : LocalDateTime.parse(r[3]);
                list.add(new TimeEntry(r[0], r[1], cin, cout, d(r[4]), d(r[5])));
            }
            return list;
        }
        static void saveAll(List<TimeEntry> items){
            List<String[]> rows = new ArrayList<>();
            for (TimeEntry t : items){
                rows.add(new String[]{
                    t.id, t.employeeId, 
                    t.clockIn == null ? "" : t.clockIn.toString(), 
                    t.clockOut == null ? "" : t.clockOut.toString(), 
                    Double.toString(t.hours), 
                    Double.toString(t.pay)
                });
            }
            CsvFiles.write(CsvFiles.p("timeclock.csv"), "id,employeeId,clockIn,clockOut,hours,pay", rows);
        }
        static Optional<TimeEntry> openShiftFor(String empId){
            return all().stream().filter(t -> empId.equals(t.employeeId) && t.clockOut==null).findFirst();
        }

        static double sumHours(String empId){
            return all().stream().filter(t -> empId.equals(t.employeeId)).mapToDouble(t -> t.hours).sum();
        }

        static double sumPay(String empId){
            return all().stream().filter(t -> empId.equals(t.employeeId)).mapToDouble(t -> t.pay).sum();
        }
    }

    // parsing helpers
    static int i(String s){ try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; } }
    static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
    static boolean b(String s){ return "true".equalsIgnoreCase(s.trim()); }
    static String n(String s){ return (s==null || s.isBlank())? null : s; }
    static String nn(String s){ return s==null? "" : s; }

    // sales compute
    static void computeTotals(Sale sale){
        double sub=0, tax=0;
        for (SaleLine l : sale.lines){ sub += l.lineTotal; if (l.taxable) tax += l.lineTotal*TAX_RATE; }
        sale.subTotal=sub; sale.tax=tax; sale.total=sub+tax;
    }

    // ===================== Panels =====================

    // ---- Store (Inventory & Item Sales) ----
    class StorePanel extends JPanel {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"SKU","Name","Category","Price","Qty","Taxable"}, 0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);

        StorePanel(){
            setLayout(new BorderLayout());
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton add = new JButton("Add Item");
            JButton edit = new JButton("Edit");
            JButton del = new JButton("Delete");
            JButton sell = new JButton("New Sale");
            actions.add(add); actions.add(edit); actions.add(del); actions.add(sell);
            add(actions, BorderLayout.NORTH);

            // Haylee - Wrapped action listeners with error handling
            add.addActionListener(e -> Err.safe(this,"adding an item", this::onAdd).run());
            edit.addActionListener(e -> Err.safe(this,"editing an item", this::onEdit).run());
            del.addActionListener(e -> Err.safe(this,"deleting an item", this::onDelete).run());
            sell.addActionListener(e -> Err.safe(this,"processing a sale", this::onSell).run());

            reload();
        }

        void reload(){
            model.setRowCount(0);
            for (InventoryItem it : InventoryRepo.all()){
                // Haylee - Changed to show Yes/No instead of True/False
                model.addRow(new Object[]{it.sku,it.name,it.category,money(it.unitPrice),it.qtyOnHand,yn(it.taxable)});
            }
        }

        Optional<InventoryItem> selected(){
            int r = table.getSelectedRow(); if (r<0) return Optional.empty();
            String sku = (String) model.getValueAt(r,0);
            return InventoryRepo.bySku(sku);
        }

        void onAdd(){
            String sku = JOptionPane.showInputDialog(this,"SKU:"); if (sku==null||sku.isBlank()) return;
            if (InventoryRepo.bySku(sku).isPresent()){ JOptionPane.showMessageDialog(this,"SKU exists."); return; }
            String name = JOptionPane.showInputDialog(this,"Name:"); if (name==null) return;
            String cat = JOptionPane.showInputDialog(this,"Category:"); if (cat==null) return;
            double price = d(JOptionPane.showInputDialog(this,"Unit Price:"));
            int qty = i(JOptionPane.showInputDialog(this,"Qty On Hand:"));
            int tax = JOptionPane.showConfirmDialog(this,"Taxable?","Tax",JOptionPane.YES_NO_OPTION);
            boolean taxable = (tax==JOptionPane.YES_OPTION);
            List<InventoryItem> all = InventoryRepo.all();
            all.add(new InventoryItem(null, sku, name, cat, price, qty, taxable));
            InventoryRepo.saveAll(all);
            reload();
        }

        void onEdit(){
            Optional<InventoryItem> opt = selected(); if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Select a row"); return; }
            InventoryItem it = opt.get();
            String name = JOptionPane.showInputDialog(this,"Name:", it.name); if (name==null) return;
            String cat = JOptionPane.showInputDialog(this,"Category:", it.category); if (cat==null) return;
            double price = d(JOptionPane.showInputDialog(this,"Unit Price:", Double.toString(it.unitPrice)));
            int qty = i(JOptionPane.showInputDialog(this,"Qty On Hand:", Integer.toString(it.qtyOnHand)));
            boolean taxable = JOptionPane.showConfirmDialog(this,"Taxable?","Tax", it.taxable?JOptionPane.YES_OPTION:JOptionPane.NO_OPTION)==JOptionPane.YES_OPTION;
            List<InventoryItem> all = InventoryRepo.all();
            for (InventoryItem x : all) if (x.id.equals(it.id)){ x.name=name; x.category=cat; x.unitPrice=price; x.qtyOnHand=qty; x.taxable=taxable; }
            InventoryRepo.saveAll(all);
            reload();
        }

        void onDelete(){
            Optional<InventoryItem> opt = selected(); if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Select a row"); return; }
            if (JOptionPane.showConfirmDialog(this,"Delete selected item?","Confirm",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return;
            InventoryItem it = opt.get();
            List<InventoryItem> all = InventoryRepo.all();
            all.removeIf(x -> x.id.equals(it.id));
            InventoryRepo.saveAll(all);
            reload();
        }

        void onSell(){
            String sku = JOptionPane.showInputDialog(this,"Enter SKU to sell:"); if (sku==null) return;
            Optional<InventoryItem> opt = InventoryRepo.bySku(sku);
            if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Not found"); return; }
            InventoryItem it = opt.get();
            int qty = i(JOptionPane.showInputDialog(this,"Qty:"));
            if (qty<=0 || qty>it.qtyOnHand){ JOptionPane.showMessageDialog(this,"Invalid qty"); return; }

            Sale sale = new Sale();
            sale.lines.add(SaleLine.item(it, qty));
            computeTotals(sale);

            int method = JOptionPane.showOptionDialog(this,
                    "Total: "+money(sale.total)+"\\nChoose payment method",
                    "Payment", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{"Cash","Card"}, "Cash");
            if (method==0) sale.paidCash = sale.total; else sale.paidCard = sale.total;

            // commit: decrement qty, save inventory & sale
            List<InventoryItem> inv = InventoryRepo.all();
            for (InventoryItem x : inv) if (x.id.equals(it.id)) x.qtyOnHand -= qty;
            InventoryRepo.saveAll(inv);

            List<Sale> sales = SaleRepo.all();
            sales.add(sale);
            SaleRepo.saveAll(sales);

            JOptionPane.showMessageDialog(this,"Sale complete.\\nReceipt: "+sale.id);
            reload();
        }
    }

    // ---- Services (Catalog + Appointments) ----
    class ServicesPanel extends JPanel {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"When","Customer","Service","Status","Paid"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);

        ServicesPanel(){
            setLayout(new BorderLayout());
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton newAppt = new JButton("New Appointment");
            JButton donePay = new JButton("Mark Done & Pay");
            JButton addSvc = new JButton("Add Service");
            actions.add(newAppt); actions.add(donePay); actions.add(addSvc);
            add(actions, BorderLayout.NORTH);

            // Haylee - Wrapped action listeners with error handling
            newAppt.addActionListener(e -> Err.safe(this,"creating an appointment", this::onNewAppt).run());
            donePay.addActionListener(e -> Err.safe(this,"marking done & collecting payment", this::onDonePay).run());
            addSvc.addActionListener(e -> Err.safe(this,"adding a service", this::onAddService).run());

            reload();
        }

        void reload(){
            model.setRowCount(0);
            List<Appointment> appts = AppointmentRepo.all();
            List<Customer> customers = CustomerRepo.all();
            List<Service> svcs = ServiceRepo.all();
            for (Appointment a : appts){
                String cname = customers.stream().filter(c->c.id.equals(a.customerId)).map(c->c.fullName).findFirst().orElse("?");
                String sname = svcs.stream().filter(s->s.id.equals(a.serviceId)).map(s->s.name).findFirst().orElse("?");
                model.addRow(new Object[]{a.start, cname, sname, a.status, money(a.paidAmount)});
            }
        }

        void onAddService(){
            String name = JOptionPane.showInputDialog(this,"Service name:"); if (name==null||name.isBlank()) return;
            String desc = JOptionPane.showInputDialog(this,"Description:"); if (desc==null) desc="";
            double price = d(JOptionPane.showInputDialog(this,"Base price:"));
            int mins = i(JOptionPane.showInputDialog(this,"Duration (minutes):"));
            List<Service> all = ServiceRepo.all();
            all.add(new Service(null, name, desc, price, mins));
            ServiceRepo.saveAll(all);
            JOptionPane.showMessageDialog(this,"Service added.");
            reload();
        }

        void onNewAppt(){
            String custId = JOptionPane.showInputDialog(this,"Customer ID (or new like C3):"); if (custId==null) return;
            List<Customer> custs = CustomerRepo.all();
            if (custs.stream().noneMatch(c->c.id.equals(custId))){
                String nm = JOptionPane.showInputDialog(this,"Customer name:"); if (nm==null) return;
                String ph = JOptionPane.showInputDialog(this,"Phone:"); if (ph==null) ph="";
                String em = JOptionPane.showInputDialog(this,"Email:"); if (em==null) em="";
                custs.add(new Customer(custId, nm, ph, em));
                CustomerRepo.saveAll(custs);
            }
            String svcId = JOptionPane.showInputDialog(this,"Service ID (e.g., S1):"); if (svcId==null) return;
            Optional<Service> svc = ServiceRepo.byId(svcId);
            if (svc.isEmpty()){ JOptionPane.showMessageDialog(this,"Service not found"); return; }

            LocalDateTime when = LocalDateTime.now().plusDays(1);
            Appointment ap = new Appointment(null, custId, null, svcId, when, when.plusMinutes(svc.get().durationMinutes), "BOOKED", 0);

            // simple overlap check
            for (Appointment a : AppointmentRepo.all()){
                boolean overlap = a.start.isBefore(ap.end) && ap.start.isBefore(a.end);
                if (overlap){ JOptionPane.showMessageDialog(this,"Time overlaps existing appointment."); return; }
            }

            List<Appointment> all = AppointmentRepo.all(); all.add(ap); AppointmentRepo.saveAll(all);
            reload();
        }

        void onDonePay(){
            int r = table.getSelectedRow(); if (r<0){ JOptionPane.showMessageDialog(this,"Select an appointment"); return; }
            List<Appointment> all = AppointmentRepo.all();
            Appointment ap = all.get(r);
            Service svc = ServiceRepo.byId(ap.serviceId).orElse(null);
            if (svc==null){ JOptionPane.showMessageDialog(this,"Service missing"); return; }
            double pay = d(JOptionPane.showInputDialog(this,"Collect payment (base "+money(svc.basePrice)+"):", Double.toString(svc.basePrice)));
            ap.status="DONE"; ap.paidAmount=pay;
            AppointmentRepo.saveAll(all);
            JOptionPane.showMessageDialog(this,"Marked DONE. Paid "+money(pay));
            reload();
        }
    }

    // ---- Animals (Inventory & Sales) ----
    class AnimalsPanel extends JPanel {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Species","Breed","Sex","Age(m)","Price","On Hold","Supplier","Sold"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);

        AnimalsPanel(){
            setLayout(new BorderLayout());
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton add = new JButton("Add");
            JButton edit = new JButton("Edit");
            JButton sell = new JButton("Sell");
            actions.add(add); actions.add(edit); actions.add(sell);
            add(actions, BorderLayout.NORTH);

            // Haylee - Wrapped action listeners with error handling
            add.addActionListener(e -> Err.safe(this,"adding an animal", this::onAdd).run());
            edit.addActionListener(e -> Err.safe(this,"editing an animal", this::onEdit).run());
            sell.addActionListener(e -> Err.safe(this,"selling an animal", this::onSell).run());

            reload();
        }

        void reload(){
            model.setRowCount(0);
            for (Animal a : AnimalRepo.all()){
                // Haylee - Changed to show Yes/No instead of True/False for sold column
                model.addRow(new Object[]{a.id,a.species,a.breed,a.sex,a.ageMonths,money(a.price),yn(a.onHold),a.supplierName,yn(a.sold)});
            }
        }

        Optional<Animal> selected(){
            int r = table.getSelectedRow(); if (r<0) return Optional.empty();
            String id = (String) model.getValueAt(r,0);
            return AnimalRepo.all().stream().filter(a->a.id.equals(id)).findFirst();
        }

        void onAdd(){
            String species = JOptionPane.showInputDialog(this,"Species:"); if (species==null) return;
            String breed = JOptionPane.showInputDialog(this,"Breed:"); if (breed==null) return;
            String sex = JOptionPane.showInputDialog(this,"Sex (M/F):"); if (sex==null) return;
            int age = i(JOptionPane.showInputDialog(this,"Age (months):"));
            double price = d(JOptionPane.showInputDialog(this,"Price:"));
            String supplier = JOptionPane.showInputDialog(this,"Supplier:"); if (supplier==null) supplier="";
            List<Animal> all = AnimalRepo.all();
            all.add(new Animal(null, species, breed, sex, age, "", price, false, supplier, "", false));
            AnimalRepo.saveAll(all);
            reload();
        }

        void onEdit(){
            Optional<Animal> opt = selected(); if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Select"); return; }
            Animal a = opt.get();
            String breed = JOptionPane.showInputDialog(this,"Breed:", a.breed); if (breed==null) return;
            double price = d(JOptionPane.showInputDialog(this,"Price:", Double.toString(a.price)));
            boolean hold = JOptionPane.showConfirmDialog(this,"On Hold?","Hold", a.onHold?JOptionPane.YES_OPTION:JOptionPane.NO_OPTION)==JOptionPane.YES_OPTION;
            List<Animal> all = AnimalRepo.all();
            for (Animal x : all) if (x.id.equals(a.id)){ x.breed=breed; x.price=price; x.onHold=hold; }
            AnimalRepo.saveAll(all);
            reload();
        }

        void onSell(){
            Optional<Animal> opt = selected(); if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Select"); return; }
            Animal a = opt.get();
            if (a.onHold){ JOptionPane.showMessageDialog(this,"Animal is on hold."); return; }
            if (a.sold){ JOptionPane.showMessageDialog(this,"Already sold."); return; }

            Sale sale = new Sale();
            sale.lines.add(SaleLine.animal(a));
            computeTotals(sale);
            sale.paidCard = sale.total; // simple

            // commit: mark animal sold, save animal + sale
            List<Animal> animals = AnimalRepo.all();
            for (Animal x : animals) if (x.id.equals(a.id)) x.sold = true;
            AnimalRepo.saveAll(animals);

            List<Sale> sales = SaleRepo.all(); sales.add(sale); SaleRepo.saveAll(sales);

            JOptionPane.showMessageDialog(this,"Sold. Receipt: "+sale.id+"  Total: "+money(sale.total));
            reload();
        }
    }

    // ---- Employees and Time Clock ----
    // Haylee - EmployeesPanel class
    class EmployeesPanel extends JPanel {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Name","Hourly Rate","Active","Clocked In"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);

        EmployeesPanel() {
            setLayout(new BorderLayout());
            add(new JScrollPane(table), BorderLayout.CENTER);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton add = new JButton("Add Employee");
            JButton edit = new JButton("Edit Employee");
            JButton toggle = new JButton("Toggle Active");
            JButton clockIn = new JButton("Clock In"); 
            JButton clockOut = new JButton("Clock Out"); 
            actions.add(add); actions.add(edit); actions.add(toggle); actions.add(clockIn); actions.add(clockOut);
            add(actions, BorderLayout.NORTH);

            // Haylee - Wrapped action listeners with error handling
            add.addActionListener(e -> Err.safe(this,"adding an employee", this::onAdd).run());
            edit.addActionListener(e -> Err.safe(this,"editing an employee", this::onEdit).run());
            toggle.addActionListener(e -> Err.safe(this,"toggling employee active status", this::onToggleActive).run());
            clockIn.addActionListener(e -> Err.safe(this,"clocking in", this::onClockIn).run());
            clockOut.addActionListener(e -> Err.safe(this,"clocking out", this::onClockOut).run());

            reload();
        }

        void reload() {
            model.setRowCount(0);
            List<Employee> emps = EmployeeRepo.all();
            for (Employee emp : emps) {
                boolean open = TimeRepo.openShiftFor(emp.id).isPresent();
                model.addRow(new Object[]{emp.id, emp.name, money(emp.hourlyRate), yn(emp.active), yn(open)});
            }
        }

        Optional<Employee> selected() {
            int r = table.getSelectedRow(); if (r < 0) return Optional.empty();
            String empId = (String) model.getValueAt(r, 0);
            return EmployeeRepo.byId(empId);
        }

        void onAdd() {
            String name = JOptionPane.showInputDialog(this, "Employee Name:"); if (name == null || name.isBlank()) return;
            double rate = d(JOptionPane.showInputDialog(this, "Hourly Rate:"));
            List<Employee> all = EmployeeRepo.all();
            all.add(new Employee(null, name, rate, true));
            EmployeeRepo.saveAll(all);
            reload();
        }

        void onEdit() {
            Optional<Employee> opt = selected(); if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select an employee");
                return;
            }
            Employee e = opt.get();
            String name = JOptionPane.showInputDialog(this, " Name:", e.name); if (name == null) return;
            double rate = d(JOptionPane.showInputDialog(this, "Hourly Rate:", Double.toString(e.hourlyRate)));
            List<Employee> all = EmployeeRepo.all();
            for (Employee x : all) if (x.id.equals(e.id)) { x.name = name; x.hourlyRate = rate; }
            EmployeeRepo.saveAll(all);
            reload();
        }

        void onToggleActive() {
            Optional<Employee> opt = selected(); if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select an employee");
                return;
            }
            Employee e = opt.get();
            List<Employee> all = EmployeeRepo.all();
            for (Employee x : all) if (x.id.equals(e.id)) x.active = !x.active;
            EmployeeRepo.saveAll(all);
            reload();
        }

        void onClockIn() {
            Optional<Employee> opt = selected(); if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select an employee");
                return;
            }
            Employee e = opt.get();
            if (!e.active) {
                JOptionPane.showMessageDialog(this, "Employee is inactive.");
                return;
            }
            if (TimeRepo.openShiftFor(e.id).isPresent()) {
                JOptionPane.showMessageDialog(this, "Employee already clocked in.");
                return;
            }
            List<TimeEntry> ts = TimeRepo.all();
            ts.add(new TimeEntry(null, e.id, LocalDateTime.now(), null, 0.0, 0.0));
            TimeRepo.saveAll(ts);
            JOptionPane.showMessageDialog(this, e.name + "clocked in.");
            reload();
        }

        void onClockOut() {
            Optional<Employee> opt = selected(); if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select an employee");
                return;
            }
            Employee e = opt.get();
            Optional<TimeEntry> openOpt = TimeRepo.openShiftFor(e.id);
            if (openOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee is not clocked in.");
                return;
            }
            TimeEntry open = openOpt.get();
            LocalDateTime out = LocalDateTime.now();
            double hours = Math.max(0.0, Duration.between(open.clockIn, out).toMinutes() / 60.0);
            double pay = hours * e.hourlyRate;

            List<TimeEntry> all = TimeRepo.all();
            for (TimeEntry t : all) {
                if (t.id.equals(open.id)) {
                    t.clockOut = out;
                    t.hours = hours;
                    t.pay = pay;
                }
            }
            TimeRepo.saveAll(all);
            JOptionPane.showMessageDialog(this, String.format("%s clocked out. Hours: %.2f Pay: %s", e.name, hours, money(pay)));
            reload();
        }
    }

    // ---- Reports ----
    class ReportsPanel extends JPanel {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Metric","Value"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);
        ReportsPanel(){
            setLayout(new BorderLayout());
            add(new JScrollPane(table), BorderLayout.CENTER);
            JButton refresh = new JButton("Refresh");
            add(refresh, BorderLayout.NORTH);
            // Haylee - Wrapped action listener with error handling
            refresh.addActionListener(e -> Err.safe(this,"refreshing reports", this::reload).run());
            reload();
        }
        void reload(){
            model.setRowCount(0);
            List<Sale> sales = SaleRepo.all();
            double total = sales.stream().mapToDouble(s->s.total).sum();
            model.addRow(new Object[]{"Lifetime Sales", money(total)});
            model.addRow(new Object[]{"Receipts", sales.size()});

            // Haylee - Employee hours and pay report
            double empHours = EmployeeRepo.all().stream().mapToDouble(e->TimeRepo.sumHours(e.id)).sum();
            double empPay = EmployeeRepo.all().stream().mapToDouble(e->TimeRepo.sumPay(e.id)).sum();
            model.addRow(new Object[]{"Total Employee Hours", String.format("%.2f", empHours)});
            model.addRow(new Object[]{"Total Employee Pay", money(empPay)});
        }
    }
}