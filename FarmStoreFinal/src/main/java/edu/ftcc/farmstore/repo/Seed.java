package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.util.PathsCfg;
import edu.ftcc.farmstore.util.Csv;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Seed {
    public static void ensureAll() {

        // ---------- inventory.csv ----------
        List<String[]> inventorySeed = Arrays.asList(
            new String[] {"I1", "DOGFOOD-20", "Dog Kibble 20lb", "Food", "29.99", "12", "true"},
            new String[] {"I2", "CATTOY-FEATHER", "Feather Toy", "Toys", "6.49", "25", "true"}
        );
        Csv.ensureWithSeed(
            PathsCfg.p("inventory.csv"),
            "id,sku,name,category,unitPrice,qtyOnHand,taxable",
            inventorySeed
        );

        // ---------- animals.csv ----------
        List<String[]> animalsSeed = Arrays.asList(
            new String[] {"A1", "Chicken", "Silkie", "F", "6", "", "15.00", "false", "Local Breeder", "Healthy", "false"},
            new String[] {"A2", "Rabbit", "Mini Rex", "M", "4", "", "45.00", "false", "Local Breeder", "Calm", "false"}
        );
        Csv.ensureWithSeed(
            PathsCfg.p("animals.csv"),
            "id,species,breed,sex,ageMonths,microchipId,price,onHold,supplierName,notes,sold",
            animalsSeed
        );

        // ---------- customers.csv ----------
        List<String[]> customersSeed = Arrays.asList(
            new String[] {"C1", "Jane Doe", "910-555-0001", "jane@example.com"},
            new String[] {"C2", "Bob Smith", "910-555-0002", "bob@example.com"}
        );
        Csv.ensureWithSeed(
            PathsCfg.p("customers.csv"),
            "id,fullName,phone,email",
            customersSeed
        );

        // ---------- services.csv ----------
        List<String[]> servicesSeed = Arrays.asList(
            new String[] {"S1", "Nail Trim", "Basic trim", "15.00", "15"},
            new String[] {"S2", "Wellness Check", "Quick exam", "45.00", "30"}
        );
        Csv.ensureWithSeed(
            PathsCfg.p("services.csv"),
            "id,name,description,basePrice,durationMinutes",
            servicesSeed
        );

        // ---------- appointments.csv ----------
        List<String[]> appointmentsSeed = Collections.<String[]>emptyList();
        Csv.ensureWithSeed(
            PathsCfg.p("appointments.csv"),
            "id,customerId,animalId,serviceId,start,end,status,paidAmount",
            appointmentsSeed
        );

        // ---------- sales.csv ----------
        List<String[]> salesSeed = Collections.<String[]>emptyList();
        Csv.ensureWithSeed(
            PathsCfg.p("sales.csv"),
            "id,customerId,dateTime,subTotal,tax,total,paidCash,paidCard,lines",
            salesSeed
        );

        // ---------- employees.csv ----------
        List<String[]> employeesSeed = new java.util.ArrayList<>();
        employeesSeed.add(new String[] {"E1", "Alex Johnson", "12.50", "true"});

        Csv.ensureWithSeed(
            PathsCfg.p("employees.csv"),
            "id,name,hourlyRate,active",
            employeesSeed
        );

        // ---------- time_entries.csv ----------
        List<String[]> timeEntriesSeed = Collections.<String[]>emptyList();
        Csv.ensureWithSeed(
            PathsCfg.p("time_entries.csv"),
            "id,employeeId,clockIn,clockOut,hours,pay",
            timeEntriesSeed
        );

        // ---------- lifetime_transactions.csv ----------
        List<String[]> lifetimeSeed = Collections.<String[]>emptyList();
        Csv.ensureWithSeed(
            PathsCfg.p("lifetime_transactions.csv"),
            "date,receiptId,items,price",
            lifetimeSeed
        );
    }
}
