package com.hyberbin.dubbo.client.download;

import com.hyberbin.dubbo.client.model.DependencyModel;
import java.io.File;
import java.net.URLClassLoader;

public interface JarDownloader {

    /**
     * 根据依赖批量下载
     */
    URLClassLoader getClassLoader(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies) throws Exception;

    /**
     * 下载单个jar
     *
     * @param dir 下载到哪个目录
     * @param mainDependency 主依赖是哪个
     * @param isSource 是否是下载源码包
     */
    File getJarFile(String dir, DependencyModel mainDependency, boolean isSource) throws Exception;
}
