package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import edu.ftcc.farmstore.repo.LifetimeRepo;

public class ReportsPanel extends JPanel {
    public ReportsPanel(){
        setLayout(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Lifetime Transactions (raw rows)\\n");
        for (String[] r : LifetimeRepo.all()){
            sb.append(String.join(" | ", r)).append("\\n");
        }
        area.setText(sb.toString());
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}
