package bv;

import bv.domain.ScheduleTask;
import bv.service.CICOService;
import bv.service.CICOServiceImpl;
import bv.utils.FileUtils;
import bv.utils.ObjectUtils;
import bv.utils.PopupUtils;
import bv.view.AutoCICOFrame;
import bv.view.MenuBar;
import com.apple.eawt.Application;

import java.awt.desktop.ScreenSleepEvent;
import java.awt.desktop.ScreenSleepListener;
import java.io.File;
import java.util.List;

import static bv.utils.Constant.*;
import static bv.utils.FileUtils.rootPath;

public class Main {

    public static void main(String[] args) {
        System.setProperty("apple.awt.UIElement", "true");
        createRootFolderAndInitFile();
        CICOService cicoService = CICOServiceImpl.getInstance();
        cicoService.checkinCheckoutWithToken(FileUtils.loadFromFile(TOKEN_FILE));
        javax.swing.SwingUtilities.invokeLater(() -> new MenuBar(new AutoCICOFrame()));

        if (isMacOs()) {
            Application application = Application.getApplication();

            application.addAppEventListener(new ScreenSleepListener() {
                @Override
                public void screenAboutToSleep(ScreenSleepEvent e) {
                    ObjectUtils.callFunction(() -> cicoService.checkinCheckoutWithToken(FileUtils.loadFromFile(TOKEN_FILE)));
                }

                @Override
                public void screenAwoke(ScreenSleepEvent e) {
                    ObjectUtils.callFunction(() -> cicoService.checkinCheckoutWithToken(FileUtils.loadFromFile(TOKEN_FILE)));
                }
            });
        }

        cicoService.autoCICO(List.of(new ScheduleTask(8, 25),
                new ScheduleTask(12, 1),
                new ScheduleTask(15, 1),
                new ScheduleTask(17, 0)
        ), PopupUtils::showSuccess);
    }

    private static boolean isMacOs() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private static void createRootFolderAndInitFile() {
        File folder = new File(rootPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        List.of(ACCOUNT_FILE, TOKEN_FILE, SETTING_FILE)
                .forEach(fileName -> {
                    ObjectUtils.callFunction(() -> {
                        File file = new File(rootPath + fileName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    });
                });
    }
}
