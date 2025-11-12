package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import edu.ftcc.farmstore.repo.Seed;

public class MainFrame extends JFrame {
    public MainFrame(){
        super("Farm Store Manager — Multi-file");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);

        // Seed CSVs on first run
        Seed.ensureAll();

        var tabs = new JTabbedPane();
        tabs.addTab("Store", new StorePanel());
        tabs.addTab("Services", new ServicesPanel());
        tabs.addTab("Animals", new AnimalsPanel());
        tabs.addTab("Employees", new EmployeesPanel());
        tabs.addTab("Reports", new ReportsPanel());
        add(tabs, BorderLayout.CENTER);
    }
}
