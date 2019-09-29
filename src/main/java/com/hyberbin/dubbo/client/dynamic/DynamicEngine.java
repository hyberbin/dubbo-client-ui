package com.hyberbin.dubbo.client.dynamic;

import com.hyberbin.dubbo.client.exception.DubboClientUException;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import lombok.extern.slf4j.Slf4j;


/**
 * 在Java SE6中最好的方法是使用StandardJavaFileManager类。 这个类可以很好地控制输入、输出，并且可以通过DiagnosticListener得到诊断信息，
 * 而DiagnosticCollector类就是listener的实现。 使用StandardJavaFileManager需要两步。
 * 首先建立一个DiagnosticCollector实例以及通过JavaCompiler的getStandardFileManager()方法得到一个StandardFileManager对象。
 * 最后通过CompilationTask中的call方法编译源程序。
 */
@Slf4j
public class DynamicEngine {

    //单例
    private static DynamicEngine instance = new DynamicEngine();

    private DynamicEngine() {

    }

    public static DynamicEngine getInstance() {
        return instance;
    }

    /**
     * @MethodName : 创建classpath
     */
    private String buildClassPath(URLClassLoader parentClassLoader) {
        StringBuilder sb = new StringBuilder();
        String classResource = this.getClass().getName().replace(".", "/");
        String thisPath = getClass().getResource("/" + classResource + ".class")
                .getFile();
        thisPath = thisPath.split("!")[0].replace("file:", "");
        sb.append(thisPath).append(File.pathSeparator);
        for (URL url : parentClassLoader.getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        return sb.toString();
    }

    /**
     * @param fullClassName 类名
     * @param javaCode 类代码
     * @return Object
     * @MethodName : 编译java代码到Object
     * @Description : TODO
     */
    public Class javaCodeToClass(String fullClassName, String javaCode,
            URLClassLoader parentClassLoader) throws Exception {
        Class aClass = null;
        //获取系统编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            log.error("运行时缺少tools.jar,不支持动态编译技术");
            throw new DubboClientUException("运行时缺少tools.jar");
        }
        // 建立DiagnosticCollector对象
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        // 建立用于保存被编译文件名的对象
        // 每个文件被保存在一个从JavaFileObject继承的类中
        ClassFileManager fileManager = new ClassFileManager(
                compiler.getStandardFileManager(diagnostics, Locale.CHINA,
                        Charset.forName("utf-8")));

        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));

        //使用编译选项可以改变默认编译行为。编译选项是一个元素为String类型的Iterable集合
        List<String> options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(buildClassPath(parentClassLoader));

        JavaCompiler.CompilationTask task = compiler
                .getTask(null, fileManager, diagnostics, options, null, jfiles);
        // 编译源程序
        boolean success = task.call();

        if (success) {
            //如果编译成功，用类加载器加载该类
            JavaClassObject jco = fileManager.getJavaClassObject();
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(parentClassLoader);
            aClass = dynamicClassLoader.loadClass(fullClassName, jco);
        } else {
            //如果想得到具体的编译错误，可以对Diagnostics进行扫描
            String error = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                error += compilePrint(diagnostic);
            }
            log.error(fullClassName);
            log.error(javaCode);
            log.error(error);
        }
        return aClass;
    }

    /**
     * @param fullClassName 类名
     * @param javaCode 类代码
     * @return Object
     * @MethodName : 编译java代码到Object
     * @Description : TODO
     */
    public Object javaCodeToObject(String fullClassName, String javaCode,
            URLClassLoader parentClassLoader) throws Exception {
        return javaCodeToClass(fullClassName, javaCode, parentClassLoader).newInstance();
    }

    /**
     * @MethodName : compilePrint
     * @Description : 输出编译错误信息
     */
    private String compilePrint(Diagnostic diagnostic) {
        System.out.println("Code:" + diagnostic.getCode());
        System.out.println("Kind:" + diagnostic.getKind());
        System.out.println("Position:" + diagnostic.getPosition());
        System.out.println("Start Position:" + diagnostic.getStartPosition());
        System.out.println("End Position:" + diagnostic.getEndPosition());
        System.out.println("Source:" + diagnostic.getSource());
        System.out.println("Message:" + diagnostic.getMessage(null));
        System.out.println("LineNumber:" + diagnostic.getLineNumber());
        System.out.println("ColumnNumber:" + diagnostic.getColumnNumber());
        StringBuffer res = new StringBuffer();
        res.append("Code:[" + diagnostic.getCode() + "]\n");
        res.append("Kind:[" + diagnostic.getKind() + "]\n");
        res.append("Position:[" + diagnostic.getPosition() + "]\n");
        res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
        res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
        res.append("Source:[" + diagnostic.getSource() + "]\n");
        res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
        res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
        res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
        return res.toString();
    }
}