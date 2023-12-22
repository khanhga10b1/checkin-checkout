package bv.view;

import bv.utils.ObjectUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuBar {
    private JFrame frame;


    private MenuBar() {
    }

    public MenuBar(JFrame frame) {
        this.frame = frame;
        initComponent();
    }

    private void initComponent() {

        if (!SystemTray.isSupported()) {
            System.err.println("SystemTray is not supported on this platform.");
            return;
        }

        Image trayIconImage = ObjectUtils.getIgnoreException(() -> ImageIO.read(ClassLoader.getSystemResource("application.png")));

        if (trayIconImage == null) {
            System.out.println("Null icon");
            return;
        }

        PopupMenu popup = new PopupMenu();
        MenuItem openMenuItem = new MenuItem("Hide Application");
        openMenuItem.addActionListener(e -> {
            if (!frame.isDisplayable()) {
                frame.setVisible(true);
            } else {
                frame.dispose();
            }
            openMenuItem.setLabel(!frame.isDisplayable() ? "Show Application" : "Hide Application");
        });
        popup.add(openMenuItem);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                openMenuItem.setLabel("Show Application");
            }
        });

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        popup.add(exitMenuItem);


        TrayIcon trayIcon = new TrayIcon(trayIconImage, "Your Application", popup);
        trayIcon.setImageAutoSize(true);
        ObjectUtils.callFunction(() -> SystemTray.getSystemTray().add(trayIcon));
    }
}
