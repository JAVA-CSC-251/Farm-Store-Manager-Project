package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import edu.ftcc.farmstore.model.InventoryItem;
import edu.ftcc.farmstore.repo.InventoryRepo;
import edu.ftcc.farmstore.util.Ids;

public class StorePanel extends JPanel {
    DefaultTableModel model = new DefaultTableModel(new Object[]{"SKU","Name","Category","Price","Qty","Taxable"},0){
        public boolean isCellEditable(int r,int c){ return false; }
    };
    JTable table = new JTable(model);

    public StorePanel(){
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Item");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        actions.add(add); actions.add(edit); actions.add(del);
        add(actions, BorderLayout.NORTH);

        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());

        reload();
    }

    private void reload(){
        model.setRowCount(0);
        for (InventoryItem it : InventoryRepo.all()){
            model.addRow(new Object[]{it.sku,it.name,it.category,String.format("$%.2f",it.unitPrice),it.qtyOnHand, it.taxable? "Yes":"No"});
        }
    }
    private Optional<InventoryItem> selected(){
        int r = table.getSelectedRow(); if (r<0) return Optional.empty();
        String sku = (String) model.getValueAt(r,0);
        return InventoryRepo.bySku(sku);
    }
    private void onAdd(){
        String sku = JOptionPane.showInputDialog(this,"SKU:"); if (sku==null||sku.isBlank()) return;
        if (InventoryRepo.bySku(sku).isPresent()){ JOptionPane.showMessageDialog(this,"SKU exists."); return; }
        String name = JOptionPane.showInputDialog(this,"Name:"); if (name==null) return;
        String cat = JOptionPane.showInputDialog(this,"Category:"); if (cat==null) return;
        double price = d(JOptionPane.showInputDialog(this,"Unit Price:"));
        int qty = i(JOptionPane.showInputDialog(this,"Qty On Hand:"));
        int tax = JOptionPane.showConfirmDialog(this,"Taxable?","Tax",JOptionPane.YES_NO_OPTION);
        boolean taxable = (tax==JOptionPane.YES_OPTION);
        java.util.List<InventoryItem> all = InventoryRepo.all();
        all.add(new InventoryItem(Ids.id("I"), sku, name, cat, price, qty, taxable));
        InventoryRepo.saveAll(all);
        reload();
    }
    private void onEdit(){
    var optSel = selected();
    if (optSel.isEmpty()){
        JOptionPane.showMessageDialog(this,"Select a row");
        return;
    }
    var it = optSel.get();                 // currently selected item
    String oldSku = it.sku;                // remember the old SKU

    // Prompt all fields, including SKU
    String newSku = JOptionPane.showInputDialog(this, "SKU:", it.sku);
    if (newSku == null || newSku.isBlank()) return;

    // If SKU changed, make sure it's unique
    if (!newSku.equalsIgnoreCase(oldSku) && InventoryRepo.bySku(newSku).isPresent()){
        JOptionPane.showMessageDialog(this, "That SKU already exists. Choose a different one.");
        return;
    }

    String name = JOptionPane.showInputDialog(this,"Name:", it.name);
    if (name == null) return;

    String cat = JOptionPane.showInputDialog(this,"Category:", it.category);
    if (cat == null) return;

    double price = d(JOptionPane.showInputDialog(this,"Unit Price:", Double.toString(it.unitPrice)));
    int qty = i(JOptionPane.showInputDialog(this,"Qty On Hand:", Integer.toString(it.qtyOnHand)));

    int opt = JOptionPane.showConfirmDialog(this,"Taxable?","Tax",JOptionPane.YES_NO_OPTION);
    boolean taxable = (opt == JOptionPane.YES_OPTION);

    // Apply edits
    var all = InventoryRepo.all();
    for (var x : all){
        if (x.id.equals(it.id)){           // match by id (stable), not SKU
            x.sku = newSku;
            x.name = name;
            x.category = cat;
            x.unitPrice = price;
            x.qtyOnHand = qty;
            x.taxable = taxable;
            break;
        }
    }

    InventoryRepo.saveAll(all);
    reload();
}


    private void onDelete(){
        var opt = selected(); if (opt.isEmpty()){ JOptionPane.showMessageDialog(this,"Select a row"); return; }
        var it = opt.get();
        var all = new ArrayList<>(InventoryRepo.all());
        all.removeIf(x -> x.id.equals(it.id));
        InventoryRepo.saveAll(all);
        reload();
    }

    private static int i(String s){ try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; } }
    private static double d(String s){ try { return Double.parseDouble(s.trim()); } catch(Exception e){ return 0.0; } }
}
