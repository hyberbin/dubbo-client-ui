package com.hyberbin.dubbo.client.download;

import com.hyberbin.dubbo.client.exception.DubboClientUException;
import com.hyberbin.dubbo.client.model.DependencyModel;
import com.hyberbin.dubbo.client.parser.ApiNormalStyleParser;
import com.hyberbin.dubbo.client.utils.AppUtils;
import com.hyberbin.dubbo.client.utils.ShellUtils;
import com.hyberbin.dubbo.client.utils.VelocityUtils;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MvnDownloader implements JarDownloader {

    private static final String shellExt =
            System.getProperty("os.name").toLowerCase().contains("windows") ? ".bat" : ".sh";

    @Override
    public URLClassLoader getClassLoader(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies) throws Exception {
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        Map map = new HashMap<>();
        List<DependencyModel> list = new ArrayList<>();
        list.add(mainDependency);
        if (dependencies != null) {
            for (DependencyModel ext : dependencies) {
                list.add(ext);
            }
        }

        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            map.put("mainDependency", mainDependency);
            map.put("dependencies", list);
            map.put("path", dir);
            dirFile.mkdirs();
            VelocityUtils.merge(dir + "pom.xml", map, "pom.vm");
            VelocityUtils.merge(dir + "mvnDependencyCopy" + shellExt, map, "mvnDependencyCopy.vm");
            File mvnDependencyCopy = new File(dir + "mvnDependencyCopy" + shellExt);
            int execute = ShellUtils.execute(mvnDependencyCopy);
            if (execute != 0) {
                throw new DubboClientUException("从maven批量下载jar出错");
            }
        }
        File[] files = dirFile.listFiles();
        List<URL> urls = new ArrayList(files.length);
        for (File file : files) {
            if (file.getName().startsWith(mainDependency.getArtifactId()) && !file.getName()
                    .endsWith("-sources.jar")) {
                File distFile = new File(
                        dir + mainDependency.getArtifactId() + "-" + mainDependency
                                .getVersion() + ".jar");
                file.renameTo(distFile);
                urls.add(distFile.toURI().toURL());
            } else {
                urls.add(file.toURI().toURL());
            }
        }
        AppUtils.buildClassPath(urls);
        System.out.println(urls);
        return new URLClassLoader(urls.toArray(new URL[]{}),
                ApiNormalStyleParser.class.getClassLoader());
    }

    @Override
    public File getJarFile(String dir, DependencyModel mainDependency, boolean isSource)
            throws Exception {
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        File distFile=new File(
                dir + mainDependency.getArtifactId() + "-" + mainDependency.getVersion()
                        + (isSource ? "-sources.jar" : ".jar"));
        if(distFile.exists()){
            return distFile;
        }
        log.info("下载{}包:{}", isSource ? "源码" : "", mainDependency.getArtifactId());
        Map map = new HashMap();
        map.put("mainDependency", mainDependency);
        map.put("path", dir);
        if (isSource) {
            map.put("sources", ":sources");
        }
        VelocityUtils.merge(dir + "mvnSourceCopy" + shellExt, map, "mvnSourceCopy.vm");
        File mvnSourceCopy = new File(dir + "mvnSourceCopy" + shellExt);
        if (mvnSourceCopy.exists()) {
            int execute = ShellUtils.execute(mvnSourceCopy);
            if (execute == 0) {
                return distFile;
            }
        }
        throw new DubboClientUException("从maven下载jar-source出错");
    }
}
