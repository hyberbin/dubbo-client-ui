package com.hyberbin.dubbo.client.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.SneakyThrows;
import org.jplus.util.FileCopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarUtils {

    private static final Logger logger = LoggerFactory.getLogger(JarUtils.class);

    @SneakyThrows
    public static File getPom(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try (ZipFile jarFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().startsWith("META-INF/maven/") && jarEntry.getName()
                        .endsWith("pom.xml")) {
                    File pomFile = new File(
                            System.getProperty("java.io.tmpdir") + file.getName() + ".pom");
                    try (OutputStream outputStream = new FileOutputStream(pomFile)) {
                        FileCopyUtils.copy(jarFile.getInputStream(jarEntry), outputStream);
                    }
                    return pomFile;
                }
            }
        } catch (Throwable e) {
            logger.error("读取jar包中的META-INF/maven/pom.xml信息失败",e);
        }
        return null;
    }
}
