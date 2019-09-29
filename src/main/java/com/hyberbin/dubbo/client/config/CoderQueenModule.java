package com.hyberbin.dubbo.client.config;


import com.hyberbin.dubbo.client.analyse.GenericParameterAnalyser;
import com.hyberbin.dubbo.client.analyse.JavaMethodAnalyser;
import com.hyberbin.dubbo.client.analyse.JavaParameterAnalyser;
import com.hyberbin.dubbo.client.analyse.JavaSourceAnalyser;
import com.hyberbin.dubbo.client.aop.ApiRunMethodInvocation;
import com.hyberbin.dubbo.client.aop.AsyncMethodInvocation;
import com.hyberbin.dubbo.client.dao.SqliteDao;
import com.hyberbin.dubbo.client.download.JarDownloader;
import com.hyberbin.dubbo.client.download.MvnDownloader;
import com.hyberbin.dubbo.client.parser.ApiNormalStyleParser;
import com.hyberbin.dubbo.client.parser.ApiParser;
import com.hyberbin.dubbo.client.runner.ApiRunner;
import com.hyberbin.dubbo.client.runner.DubboRunner;
import com.hyberbin.dubbo.client.ui.frames.AddAppFrame;
import com.hyberbin.dubbo.client.ui.frames.DubboUIFrame;
import com.hyberbin.dubbo.client.ui.frames.DubboConfJFrame;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class CoderQueenModule extends AbstractModule {

    public static final Injector INJECTOR = Guice.createInjector(new CoderQueenModule());

    public static <T> T getInstance(Class<? extends T> clazz, String name) {
        return INJECTOR.getInstance(Key.get(clazz, Names.named(name)));
    }

    public static <T> T getInstance(Class<? extends T> clazz) {
        return INJECTOR.getInstance(clazz);
    }

    @Override
    protected void configure() {
        bind(SqliteDao.class).in(Scopes.SINGLETON);
        bind(DubboUIFrame.class).in(Scopes.SINGLETON);
        bind(DubboConfJFrame.class).in(Scopes.SINGLETON);
        bind(AddAppFrame.class).in(Scopes.SINGLETON);
        bindSingleton(JavaMethodAnalyser.class, "javaMethodAnalyser", JavaMethodAnalyser.class);
        bindSingleton(JavaParameterAnalyser.class, "javaParameterAnalyser",
                JavaParameterAnalyser.class);
        bindSingleton(GenericParameterAnalyser.class, "genericParameterAnalyser",
                GenericParameterAnalyser.class);
        bindSingleton(JavaSourceAnalyser.class, "javaSourceAnalyser", JavaSourceAnalyser.class);
        bindSingleton(ApiParser.class, "apiNormalStyleParser", ApiNormalStyleParser.class);
        bindSingleton(ApiRunner.class, "DUBBORunner", DubboRunner.class);
        bindSingleton(JarDownloader.class, "mvnDownloader", MvnDownloader.class);
        bindInterceptor(Matchers.subclassesOf(ApiRunner.class), Matchers.any(),
                new ApiRunMethodInvocation());
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Async.class),
                new AsyncMethodInvocation());
    }

    private void bindSingleton(Class type, String name, Class impl) {
        bind(type).annotatedWith(Names.named(name)).to(impl).in(Scopes.SINGLETON);
    }
}
