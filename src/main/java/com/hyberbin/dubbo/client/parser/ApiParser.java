package com.hyberbin.dubbo.client.parser;

import com.hyberbin.dubbo.client.model.AppModel;
import com.hyberbin.dubbo.client.model.DependencyModel;
import java.net.URLClassLoader;

public interface ApiParser {

    AppModel parseAppModel(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies);

    URLClassLoader getClassLoader(String dir, DependencyModel mainDependency,
            DependencyModel... dependencies);
}
