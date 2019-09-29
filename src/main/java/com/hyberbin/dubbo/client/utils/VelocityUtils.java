package com.hyberbin.dubbo.client.utils;

import com.hyberbin.dubbo.client.model.ApiModel;
import com.hyberbin.dubbo.client.model.AppModel;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.plexus.util.FileUtils;
@Slf4j
public class VelocityUtils {

  private static VelocityEngine velocityEngine;

  static {
    Properties properties = new Properties();            // 初始化参数
    properties.setProperty("file.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
    properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
    velocityEngine = new VelocityEngine(properties);
  }

  public String sharp(int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append("#");
    }
    return sb.toString();
  }

  @SneakyThrows
  public Class getClass(String name){
    return ClassUtils.getClass(Thread.currentThread().getContextClassLoader(),name);
  }

  public static void merge(String path, AppModel app, ApiModel api, File template) {
    Map context = new HashMap();
    context.put("app", app);
    context.put("api", api);
    context.put("tool", new VelocityUtils());
    merge(path, context, template);
  }

  @SneakyThrows
  public static void merge(String path, Map vars, String template) {
    Template t = velocityEngine.getTemplate(template, "UTF-8");
    VelocityContext context = new VelocityContext(vars);
    FileWriter fileWriter = new FileWriter(path);
    t.merge(context, fileWriter);
    fileWriter.flush();
    fileWriter.close();
  }

  @SneakyThrows
  public static void merge(String path, Map vars, File template) {
    String exp= FileUtils.fileRead(template,"utf-8");
    String evaluate = evaluate(exp, vars);
    FileUtils.fileWrite(path,"utf-8",evaluate);
  }

  @SneakyThrows
  public static String merge(String template, Map vars) {
    Template t = velocityEngine.getTemplate(template, "UTF-8");
    VelocityContext context = new VelocityContext(vars);
    context.put("tool", new VelocityUtils());
    StringWriter writer = new StringWriter();
    t.merge(context, writer);
    writer.flush();
    writer.close();
    return writer.toString();
  }

  public static String evaluate(String exp, Map vars) {
    try {
      VelocityContext context = new VelocityContext(vars);
      StringWriter stringWriter = new StringWriter();
      velocityEngine.evaluate(context, stringWriter, exp, exp);
      stringWriter.flush();
      return stringWriter.toString();
    } catch (Throwable e) {
      log.warn("classLoaderVelocityEngine evaluate error :" + exp, e);
    }
    return "";
  }
}
