package bv.utils;

import javax.swing.*;
import java.awt.*;

public class PopupUtils {
    private PopupUtils() {
    }


    public static void showSuccess() {
        SwingUtilities.invokeLater(() -> {
            JDialog successDialog = new JDialog();
            successDialog.setSize(200, 100);
            successDialog.setLocationRelativeTo(null);
            successDialog.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel label = new JLabel("CICO success!");
            successDialog.add(label);
            successDialog.toFront();

            javax.swing.Timer timer = new javax.swing.Timer(3000, e ->
            {
                successDialog.dispose();
                ((javax.swing.Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
            successDialog.setVisible(true);
        });
    }
}
