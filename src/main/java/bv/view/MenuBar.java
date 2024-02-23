package bv.view;

import bv.service.CICOService;
import bv.service.CICOServiceImpl;
import bv.utils.Constant;
import bv.utils.FileUtils;
import bv.utils.ObjectUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuBar {
    private JFrame frame;
    private final CICOService cicoService;


    private MenuBar() {
        this.cicoService = CICOServiceImpl.getInstance();
    }

    public MenuBar(JFrame frame) {
        this();
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
            if (frame == null) {
                setFrame(new AutoCICOFrame(), openMenuItem);
            } else {
                frame.dispose();
                frame = null;
            }
            openMenuItem.setLabel(frame == null ? "Show Application" : "Hide Application");
        });
        popup.add(openMenuItem);



        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                frame = null;
                openMenuItem.setLabel("Show Application");
            }
        });


        MenuItem CICO = new MenuItem("Checkin/Checkout");
        CICO.addActionListener(e -> cicoService.checkinCheckoutWithToken(FileUtils.loadFromFile(Constant.TOKEN_FILE)));
        popup.add(CICO);

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        popup.add(exitMenuItem);


        TrayIcon trayIcon = new TrayIcon(trayIconImage, "Your Application", popup);
        trayIcon.setImageAutoSize(true);
        ObjectUtils.callFunction(() -> SystemTray.getSystemTray().add(trayIcon));
    }

    private void setFrame(AutoCICOFrame fr, MenuItem menuItem) {
        frame = fr;
        this.frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                frame = null;
                menuItem.setLabel("Show Application");
            }
        });
    }
}
