package com.hyberbin.dubbo.client.dynamic;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自定义类加载器
 */
public class DynamicClassLoader extends URLClassLoader {
  public DynamicClassLoader(ClassLoader parent) {
    super(new URL[0], parent);
  }

  public Class findClassByClassName(String className) throws ClassNotFoundException {
    return this.findClass(className);
  }

  public Class loadClass(String fullName, JavaClassObject jco) {
    byte[] classData = jco.getBytes();
    return this.defineClass(fullName, classData, 0, classData.length);
  }
}