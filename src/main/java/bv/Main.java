package bv;

import bv.domain.ScheduleTask;
import bv.service.CICOService;
import bv.service.CICOServiceImpl;
import bv.utils.FileUtils;
import bv.utils.ObjectUtils;
import bv.view.AutoCICOFrame;
import bv.view.MenuBar;

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
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MenuBar(new AutoCICOFrame());
            cicoService.autoCICO(List.of(new ScheduleTask(8, 25),
                    new ScheduleTask(12, 1),
                    new ScheduleTask(15, 1),
                    new ScheduleTask(17, 0)
            ));
        });
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
