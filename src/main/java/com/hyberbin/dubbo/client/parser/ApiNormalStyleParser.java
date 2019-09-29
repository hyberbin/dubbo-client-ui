/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyberbin.dubbo.client.parser;

import com.hyberbin.dubbo.client.download.JarDownloader;
import com.hyberbin.dubbo.client.exception.DubboClientUException;
import com.hyberbin.dubbo.client.model.ApiGroupModel;
import com.hyberbin.dubbo.client.model.ApiModel;
import com.hyberbin.dubbo.client.model.AppModel;
import com.hyberbin.dubbo.client.model.DependencyModel;
import com.hyberbin.dubbo.client.model.DubboApiModel;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author admin
 */
public class ApiNormalStyleParser implements ApiParser {

    private static final Logger logger = LoggerFactory.getLogger(ApiNormalStyleParser.class);
    private JarDownloader mvnDownloader;

    @Inject
    public ApiNormalStyleParser(@Named("mvnDownloader") JarDownloader mvnDownloader) {
        this.mvnDownloader = mvnDownloader;
    }

    @SneakyThrows
    @Override
    public AppModel parseAppModel(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies) {
        AppModel app = new AppModel();
        URLClassLoader classLoader = getClassLoader(dir, mainDependency, dependencies);
        app.setClassLoader(classLoader);
        app.setSourceFile(getSourceFile(dir,mainDependency));
        app.setArtifactId(mainDependency.getArtifactId());
        app.setGroupId(mainDependency.getGroupId());
        app.setVersion(mainDependency.getVersion());
        app.setAppName(mainDependency.getArtifactId());
        File mainJarFile = new File(
                dir + "/" + mainDependency.getArtifactId() + "-" + mainDependency.getVersion()
                        + ".jar");
        List<ApiGroupModel> apiGroupModels = parseApiGroup(mainJarFile, classLoader);
        app.setApiGroupList(apiGroupModels);
        return app;
    }

    @SneakyThrows
    private List<ApiGroupModel> parseApiGroup(File file, URLClassLoader classLoader) {
        List<ApiGroupModel> apiGroupModels = new ArrayList<>();
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory()) {
                    String name = jarEntry.getName();
                    if (name.endsWith(".class")) {
                        ApiGroupModel apiGroupModel = new ApiGroupModel();
                        String className = name.replace("\\", "/").replace("/", ".")
                                .replace(".class", "");
                        String simpleName = className.substring(className.lastIndexOf(".") + 1);
                        apiGroupModel.setClassName(className);
                        apiGroupModel.setValue(simpleName);
                        List<ApiModel> apiModels = parseApi(apiGroupModel, classLoader);
                        if (!CollectionUtils.isEmpty(apiModels)) {
                            apiGroupModel.setApiList(apiModels);
                            apiGroupModels.add(apiGroupModel);
                        }
                    }
                }
            }
        }
        return apiGroupModels;
    }

    @SneakyThrows
    private List<ApiModel> parseApi(ApiGroupModel apiGroup, URLClassLoader classLoader) {
        List<ApiModel> apiModels = new ArrayList<>();
        Class clazz = classLoader.loadClass(apiGroup.getClassName());
        if (clazz.isInterface()) {
            Method[] declaredMethods = clazz.getMethods();
            for (Method method : declaredMethods) {
                DubboApiModel apiModel = new DubboApiModel();
                apiModels.add(apiModel);
                apiModel.setName(method.getName());
                apiModel.setId(method.getName());
                apiModel.setMethod(method);
                apiModel.setClazz(clazz);
            }
        }
        return apiModels;
    }

    @SneakyThrows
    public URLClassLoader getClassLoader(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies) {
        return getURLClassLoader(dir, mainDependency, dependencies);
    }

    private URLClassLoader getURLClassLoader(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies) {
        try {
            URLClassLoader classLoader = mvnDownloader
                    .getClassLoader(dir, mainDependency, dependencies);
            return classLoader;
        } catch (Exception e) {
            throw new DubboClientUException("下载maven依赖出错", e);
        }
    }

    private File getSourceFile(String dir, DependencyModel mainDependency){
        try {
            return mvnDownloader.getJarFile(dir,mainDependency,true);
        }catch (Throwable e){
            logger.info("下载源码失败，忽略");
        }
        return null;
    }

}
