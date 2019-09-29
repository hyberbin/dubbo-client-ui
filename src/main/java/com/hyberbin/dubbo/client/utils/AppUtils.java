package com.hyberbin.dubbo.client.utils;

import com.hyberbin.dubbo.client.config.CoderQueenModule;
import com.hyberbin.dubbo.client.exception.DubboClientUException;
import com.hyberbin.dubbo.client.exception.SystemException;
import com.hyberbin.dubbo.client.parser.ApiParser;
import com.hyberbin.dubbo.client.vo.AppModeVO;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class AppUtils {

    private static ClassLoader getAppClassLoader(AppModeVO appModeVO) {
        if (appModeVO.getAppModel().getClassLoader() != null) {
            return appModeVO.getAppModel().getClassLoader();
        }
        synchronized (AppUtils.class) {
            if (appModeVO.getAppModel().getClassLoader() != null) {
                return appModeVO.getAppModel().getClassLoader();
            }
            ApiParser apiParser = CoderQueenModule.getInstance(ApiParser.class,"apiNormalStyleParser");
            if (apiParser != null) {
                File workDir = new File(appModeVO.getWorkSpace());
                URLClassLoader classLoader = apiParser
                        .getClassLoader(workDir.getAbsolutePath(),
                                appModeVO.getAppConfig().getMainDependency(),
                                appModeVO.getAppConfig().getExtDependencies());
                appModeVO.getAppModel().setClassLoader(classLoader);
            }
        }
        return appModeVO.getAppModel().getClassLoader();
    }

    public static ClassLoader getAppClassLoaderByCache(AppModeVO appModeVO) {
        if (appModeVO.getAppModel().getClassLoader() != null) {
            return appModeVO.getAppModel().getClassLoader();
        }
        synchronized (AppUtils.class) {
            if (appModeVO.getAppModel().getClassLoader() != null) {
                return appModeVO.getAppModel().getClassLoader();
            }
            File file = new File(appModeVO.getWorkSpace());
            if (!file.exists()) {
                return getAppClassLoader(appModeVO);
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                try {
                    List<URL> urls = new ArrayList(files.length);
                    for (File file1 : files) {
                        urls.add(file1.toURI().toURL());
                    }
                    buildClassPath(urls);
                    URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[]{}),
                            AppUtils.class.getClassLoader());
                    appModeVO.getAppModel().setClassLoader(urlClassLoader);
                } catch (Exception var4) {
                    throw new SystemException("Couldn't create a classloader.", var4);
                }
            } else {
                throw new DubboClientUException(file.getName() + "不是文件夹或者没有权限访问！");
            }
        }
        return appModeVO.getAppModel().getClassLoader();
    }

    public static void buildClassPath(List<URL> urls) throws MalformedURLException {
        String thisPath = AppUtils.class
                .getResource("/com/hyberbin/dubbo/client/utils/AppUtils.class").getFile();
        if (thisPath.contains("!")) {
            thisPath = thisPath.split("!")[0].replace("file:", "");
            urls.add(new File(thisPath).toURI().toURL());
        } else if (thisPath.contains("classes")) {
            thisPath = thisPath.substring(0, thisPath.indexOf("classes")) + "classes";
            urls.add(new File(thisPath).toURI().toURL());
        }
    }
}
