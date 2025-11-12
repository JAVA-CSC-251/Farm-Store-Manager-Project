package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import edu.ftcc.farmstore.model.Animal;
import edu.ftcc.farmstore.repo.AnimalRepo;
import edu.ftcc.farmstore.util.Ids;

public class AnimalsPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Species","Breed","Sex","Age (mo)","Price","On Hold","Supplier","Notes","Sold"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public AnimalsPanel() {
        setLayout(new BorderLayout());

        // Toolbar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton hold = new JButton("Toggle Hold");
        JButton sold = new JButton("Mark Sold");
        JButton del = new JButton("Delete");
        top.add(add); top.add(edit); top.add(hold); top.add(sold); top.add(del);
        add(top, BorderLayout.NORTH);

        // Table
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        hold.addActionListener(e -> onToggleHold());
        sold.addActionListener(e -> onMarkSold());
        del.addActionListener(e -> onDelete());

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        for (Animal a : AnimalRepo.all()) {
            model.addRow(new Object[]{
                    a.id, a.species, a.breed, a.sex, a.ageMonths,
                    String.format("$%.2f", a.price),
                    a.onHold ? "Yes" : "No",
                    a.supplierName,
                    a.notes,
                    a.sold ? "Yes" : "No"
            });
        }
        if (model.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }

    private Optional<Animal> selected() {
        int r = table.getSelectedRow();
        if (r < 0) return Optional.empty();
        String id = (String) model.getValueAt(r, 0);
        for (Animal a : AnimalRepo.all()) if (a.id.equals(id)) return Optional.of(a);
        return Optional.empty();
    }

    private void onAdd() {
        String species = ask("Species:"); if (species == null) return;
        String breed   = ask("Breed:");   if (breed == null)   return;
        String sex     = ask("Sex (M/F):", "M").toUpperCase(); if (sex == null) return;
        int age        = parseInt(ask("Age (months):", "0"), 0);
        double price   = parseDouble(ask("Price:", "0.00"), 0.0);
        int onHoldOpt  = JOptionPane.showConfirmDialog(this, "On Hold?", "Hold", JOptionPane.YES_NO_OPTION);
        boolean onHold = onHoldOpt == JOptionPane.YES_OPTION;
        String supplier= ask("Supplier Name:", "");
        String notes   = ask("Notes:", "");

        List<Animal> all = AnimalRepo.all();
        all.add(new Animal(
                Ids.id("A"),
                species, breed, sex, age,
                /* microchipId */ "",
                price, onHold, supplier, notes,
                /* sold */ false
        ));
        AnimalRepo.saveAll(all);
        reload();
    }

    private void onEdit() {
        var opt = selected();
        if (opt.isEmpty()) { msg("Select a row"); return; }
        Animal a = opt.get();

        String species = ask("Species:", a.species); if (species == null) return;
        String breed   = ask("Breed:", a.breed);     if (breed == null)   return;
        String sex     = ask("Sex (M/F):", a.sex);   if (sex == null)     return;
        int age        = parseInt(ask("Age (months):", Integer.toString(a.ageMonths)), a.ageMonths);
        double price   = parseDouble(ask("Price:", Double.toString(a.price)), a.price);
        int onHoldOpt  = JOptionPane.showConfirmDialog(this, "On Hold?", "Hold", JOptionPane.YES_NO_OPTION);
        boolean onHold = onHoldOpt == JOptionPane.YES_OPTION;
        String supplier= ask("Supplier Name:", a.supplierName); if (supplier == null) return;
        String notes   = ask("Notes:", a.notes);                if (notes == null)    return;

        List<Animal> all = AnimalRepo.all();
        for (Animal x : all) {
            if (x.id.equals(a.id)) {
                x.species = species;
                x.breed = breed;
                x.sex = sex;
                x.ageMonths = age;
                x.price = price;
                x.onHold = onHold;
                x.supplierName = supplier;
                x.notes = notes;
                break;
            }
        }
        AnimalRepo.saveAll(all);
        reload();
    }

    private void onToggleHold() {
        var opt = selected();
        if (opt.isEmpty()) { msg("Select a row"); return; }
        Animal a = opt.get();
        List<Animal> all = AnimalRepo.all();
        for (Animal x : all) if (x.id.equals(a.id)) x.onHold = !x.onHold;
        AnimalRepo.saveAll(all);
        reload();
    }

    private void onMarkSold() {
        var opt = selected();
        if (opt.isEmpty()) { msg("Select a row"); return; }
        Animal a = opt.get();
        if (a.sold) { msg("Already marked as sold."); return; }
        int conf = JOptionPane.showConfirmDialog(this, "Mark this animal as SOLD?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        List<Animal> all = AnimalRepo.all();
        for (Animal x : all) if (x.id.equals(a.id)) x.sold = true;
        AnimalRepo.saveAll(all);
        reload();
    }

    private void onDelete() {
        var opt = selected();
        if (opt.isEmpty()) { msg("Select a row"); return; }
        Animal a = opt.get();
        int conf = JOptionPane.showConfirmDialog(this, "Delete this animal?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        List<Animal> all = AnimalRepo.all();
        all.removeIf(x -> x.id.equals(a.id));
        AnimalRepo.saveAll(all);
        reload();
    }

    // Helpers
    private String ask(String prompt) { return JOptionPane.showInputDialog(this, prompt); }
    private String ask(String prompt, String def) { return JOptionPane.showInputDialog(this, prompt, def); }
    private void msg(String m) { JOptionPane.showMessageDialog(this, m); }
    private int parseInt(String s, int def) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; } }
    private double parseDouble(String s, double def) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; } }
}
