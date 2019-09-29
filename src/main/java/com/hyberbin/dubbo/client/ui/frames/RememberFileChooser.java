package com.hyberbin.dubbo.client.ui.frames;

import java.io.File;
import javax.swing.JFileChooser;
import org.apache.commons.lang.StringUtils;
import org.jplus.hyb.database.sqlite.SqliteUtil;

public class RememberFileChooser extends JFileChooser {

    private static final String lastSelectPathKey = "lastSelectPath";

    public RememberFileChooser() {
        super();
        String property = SqliteUtil.getProperty(lastSelectPathKey);
        if (StringUtils.isNotBlank(property)) {
            setCurrentDirectory(new File(property));
        }
    }

    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();
        if (selectedFile != null) {
            SqliteUtil.setProperty(lastSelectPathKey, selectedFile.getAbsolutePath());
        }
        return selectedFile;
    }
}
