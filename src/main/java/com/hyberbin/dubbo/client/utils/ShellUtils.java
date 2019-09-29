package com.hyberbin.dubbo.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellUtils {

    private static final Logger logger = LoggerFactory.getLogger(ShellUtils.class);

    public static int execute(File shell) throws Exception {
        Process exec = Runtime.getRuntime().exec(shell.getAbsolutePath());
        try (InputStream errorStream = exec.getErrorStream();
                InputStream inputStream = exec.getInputStream()) {
            new ShellReader(inputStream, false).start();
            new ShellReader(errorStream, true).start();
            return exec.waitFor();
        }
    }

    static class ShellReader extends Thread {

        private InputStream is;
        private boolean isError;

        public ShellReader(InputStream is, boolean isError) {
            this.is = is;
            this.isError = isError;
        }

        @Override
        @SneakyThrows
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (isError) {
                    logger.error(line);
                } else {
                    logger.info(line);
                }
            }
        }
    }
}
