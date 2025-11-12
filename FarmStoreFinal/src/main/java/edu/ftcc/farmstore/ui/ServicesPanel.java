package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import edu.ftcc.farmstore.model.ServiceType;
import edu.ftcc.farmstore.repo.ServiceRepo;
import edu.ftcc.farmstore.util.Ids;

public class ServicesPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Name","Description","Base Price","Duration (min)"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public ServicesPanel() {
        setLayout(new BorderLayout());

        // Toolbar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        top.add(add); top.add(edit); top.add(del);
        add(top, BorderLayout.NORTH);

        // Table
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        for (ServiceType s : ServiceRepo.all()) {
            model.addRow(new Object[]{
                    s.id,
                    s.name,
                    s.description,
                    String.format("$%.2f", s.basePrice),
                    s.durationMinutes
            });
        }
        if (model.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }

    private Optional<ServiceType> selected() {
        int r = table.getSelectedRow();
        if (r < 0) return Optional.empty();
        String id = (String) model.getValueAt(r, 0);
        for (ServiceType s : ServiceRepo.all()) if (s.id.equals(id)) return Optional.of(s);
        return Optional.empty();
    }

    private void onAdd() {
        String name = ask("Service Name:");          if (name == null || name.isBlank()) return;
        String desc = ask("Description:", "");        if (desc == null) return;
        double price = parseDouble(ask("Base Price:", "0.00"), 0.0);
        int duration = parseInt(ask("Duration (minutes):", "15"), 15);

        List<ServiceType> all = ServiceRepo.all();
        all.add(new ServiceType(Ids.id("S"), name, desc, price, duration));
        ServiceRepo.saveAll(all);
        reload();
    }

    private void onEdit() {
        var opt = selected();
        if (opt.isEmpty()) { msg("Select a row"); return; }
        ServiceType s = opt.get();

        String name = ask("Service Name:", s.name);           if (name == null) return;
        String desc = ask("Description:", s.description);     if (desc == null) return;
        double price = parseDouble(ask("Base Price:", Double.toString(s.basePrice)), s.basePrice);
        int duration = parseInt(ask("Duration (minutes):", Integer.toString(s.durationMinutes)), s.durationMinutes);

        List<ServiceType> all = ServiceRepo.all();
        for (ServiceType x : all) {
            if (x.id.equals(s.id)) {
                x.name = name;
                x.description = desc;
                x.basePrice = price;
                x.durationMinutes = duration;
                break;
            }
        }
        ServiceRepo.saveAll(all);
        reload();
    }

    private void onDelete() {
        var opt = selected();
        if (opt.isEmpty()) { msg("Select a row"); return; }
        ServiceType s = opt.get();
        int conf = JOptionPane.showConfirmDialog(this, "Delete this service?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        List<ServiceType> all = ServiceRepo.all();
        all.removeIf(x -> x.id.equals(s.id));
        ServiceRepo.saveAll(all);
        reload();
    }

    // Helpers
    private String ask(String prompt) { return JOptionPane.showInputDialog(this, prompt); }
    private String ask(String prompt, String def) { return JOptionPane.showInputDialog(this, prompt, def); }
    private void msg(String m) { JOptionPane.showMessageDialog(this, m); }
    private int parseInt(String s, int def) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; } }
    private double parseDouble(String s, double def) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; } }
}
