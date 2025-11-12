package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import edu.ftcc.farmstore.model.Employee;
import edu.ftcc.farmstore.repo.EmployeeRepo;
import edu.ftcc.farmstore.util.Ids;

public class EmployeesPanel extends JPanel {
    DefaultTableModel model = new DefaultTableModel(new Object[]{"Name","Hourly Rate","Active"},0){
        public boolean isCellEditable(int r,int c){ return false; }
    };
    JTable table = new JTable(model);

    public EmployeesPanel(){
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton toggle = new JButton("Toggle Active");
        actions.add(add); actions.add(toggle);
        add(actions, BorderLayout.NORTH);

        add.addActionListener(e -> onAdd());
        toggle.addActionListener(e -> onToggle());

        reload();
    }

    private void reload(){
        model.setRowCount(0);
        for (Employee e : EmployeeRepo.all()){
            model.addRow(new Object[]{e.name, String.format("$%.2f", e.hourlyRate), e.active? "Yes":"No"});
        }
    }
    private Optional<Employee> selected(){
        int r = table.getSelectedRow(); if (r<0) return Optional.empty();
        String name = (String) model.getValueAt(r,0);
        for (Employee e: EmployeeRepo.all()) if (e.name.equals(name)) return Optional.of(e);
        return Optional.empty();
    }
    private void onAdd(){
        String name = JOptionPane.showInputDialog(this,"Employee Name:"); if (name==null||name.isBlank()) return;
        double rate = d(JOptionPane.showInputDialog(this,"Hourly Rate:"));
        var all = new ArrayList<>(EmployeeRepo.all());
        all.add(new Employee(Ids.id("E"), name, rate, true));
        EmployeeRepo.saveAll(all);
        reload();
    }
    private void onToggle(){
        var opt = selected(); if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Select a row"); return; }
        var e = opt.get();
        var all = new ArrayList<>(EmployeeRepo.all());
        for (var x: all) if (x.id.equals(e.id)) x.active = !x.active;
        EmployeeRepo.saveAll(all);
        reload();
    }

    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
}
