package edu.ftcc.farmstore;

import javax.swing.SwingUtilities;
import edu.ftcc.farmstore.ui.MainFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
