package com.hyberbin.dubbo.client.analyse;

import com.hyberbin.dubbo.client.config.CoderQueenModule;
import com.hyberbin.dubbo.client.download.JarDownloader;
import com.hyberbin.dubbo.client.model.DependencyModel;
import com.hyberbin.dubbo.client.model.ParameterWrapper;
import com.hyberbin.dubbo.client.ui.frames.DubboUIFrame;
import com.hyberbin.dubbo.client.ui.model.AppTreeBind;
import com.hyberbin.dubbo.client.utils.JarUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
@Slf4j
public class JavaSourceAnalyser {

    private JarDownloader jarDownloader;
    private AppTreeBind appTreeBind;

    @Inject
    public JavaSourceAnalyser(@Named("mvnDownloader") JarDownloader mvnDownloader) {
        this.jarDownloader = mvnDownloader;
    }

    public ParameterWrapper[] getParameter(java.lang.reflect.Parameter[] parameters,
            Method targetMethod) {
        getAppTreeBind();
        File sourceFile = appTreeBind.getModel().getSourceFile();
        return getParameter(sourceFile, parameters, targetMethod);
    }

    private ParameterWrapper[] getParameter(File sourceFile,
            java.lang.reflect.Parameter[] parameters, Method targetMethod) {
        ParameterWrapper[] parameterWrappers = new ParameterWrapper[parameters.length];
        if (parameters.length == 0) {
            return parameterWrappers;
        }
        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter parameter = parameters[i];
            parameterWrappers[i] = new ParameterWrapper();
            parameterWrappers[i].setParameter(parameter);
        }
        if (sourceFile != null) {
            try (JarFile jarFile = new JarFile(sourceFile)) {
                ZipEntry entry = jarFile.getEntry(
                        targetMethod.getDeclaringClass().getName().replace(".", "/") + ".java");
                if (entry != null) {
                    log.info("开始解析{}的源码并获取真实的方法参数名",entry.getName());
                    InputStream inputStream = jarFile.getInputStream(entry);
                    CompilationUnit javaCu = JavaParser.parse(inputStream);
                    javaCu.accept(new VoidVisitorAdapter<ParameterWrapper[]>() {
                        @Override
                        public void visit(MethodDeclaration n, ParameterWrapper[] arg) {
                            NodeList<Parameter> parameterNodes = n.getParameters();
                            if (targetMethod.getName().equals(n.getNameAsString())
                                    && targetMethod.getParameterCount() == parameterNodes
                                    .size()) {
                                for (int i = 0; i < parameterNodes.size(); i++) {
                                    Parameter parameter = parameterNodes.get(i);
                                    java.lang.reflect.Parameter source = parameters[i];
                                    String type = parameter.getTypeAsString();
                                    Type parameterizedType = source.getParameterizedType();
                                    if (parameterizedType instanceof TypeVariable) {
                                        //变量参数类型需要全配置
                                        if (!parameterizedType.getTypeName().equals(type)) {
                                            return;
                                        }
                                    } else if (!parameterizedType.getTypeName().endsWith(type)) {
                                        return;
                                    }
                                    parameterWrappers[i].setRealName(parameter.getNameAsString());
                                }
                            }
                        }
                    }, parameterWrappers);
                }else {
                    File dependencySourceFile = getDependencySourceFile(targetMethod);
                    if (dependencySourceFile != null) {
                        return getParameter(dependencySourceFile, parameters, targetMethod);
                    }
                }
            } catch (Throwable e) {
                log.error("分析源码包出错",e);
            }
        }
        return parameterWrappers;
    }


    private File getDependencySourceFile(Method targetMethod) {
        getAppTreeBind();
        String path = targetMethod.getDeclaringClass().getResource("").getPath();
        String jarPath = path.substring(5, path.lastIndexOf("!"));
        if(jarPath.endsWith("-sources.jar")){
            return new File(jarPath);
        }
        File pom = JarUtils.getPom(new File(jarPath));
        MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
        try (FileInputStream inputStream = new FileInputStream(pom)) {
            Model mode = mavenXpp3Reader.read(inputStream, false);
            String artifactId = mode.getArtifactId();
            String groupId = mode.getGroupId();
            String version = mode.getVersion();
            if(version==null){
                version=mode.getParent().getVersion();
            }
            if(groupId==null){
                groupId=mode.getParent().getGroupId();
            }
            String workSpace = appTreeBind.getAppModeVO().getWorkSpace();
            DependencyModel dependencyModel = new DependencyModel(groupId, artifactId, version);
            return jarDownloader.getJarFile(workSpace, dependencyModel, true);
        } catch (Throwable e) {
            log.warn("找不到{}的源码包依赖，忽略真实参数解析",targetMethod.getDeclaringClass().getName());
            return null;
        }
    }

    private AppTreeBind getAppTreeBind() {
        if (appTreeBind == null) {
            DubboUIFrame coderQueenUIFrame = CoderQueenModule
                    .getInstance(DubboUIFrame.class);
            appTreeBind = coderQueenUIFrame.getCurrentUserObject(AppTreeBind.class);
        }
        return appTreeBind;
    }
}
