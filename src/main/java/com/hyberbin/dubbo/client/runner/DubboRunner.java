package com.hyberbin.dubbo.client.runner;

import com.hyberbin.dubbo.client.config.ConfigFactory;
import com.hyberbin.dubbo.client.config.RuntimeApiContext;
import com.hyberbin.dubbo.client.config.RuntimeApiContext.ApiContext;
import com.hyberbin.dubbo.client.domain.DubboConfDO;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import lombok.Getter;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboRunner implements ApiRunner {

    private static final Logger logger = LoggerFactory.getLogger(DubboRunner.class);
    private static final Map<String, DubboApplication> APPLICATION_CONFIG_MAP = new HashMap<>();
    private static final ApplicationConfig application = new ApplicationConfig("coderQueen");

    static class DubboApplication {

        private final Map<Class, ReferenceConfig> REFERENCE_CONFIG_MAP = new HashMap<>();
        private final Map<Class, Map<String, MethodConfig>> METHOD_CONFIG_MAP = new HashMap<>();
        private final Map<Class, Object> SERVICE_CLASS_MAP = new HashMap<>();
        @Getter
        private final String name;

        public DubboApplication(String name) {
            this.name = name;
        }

        private void initDubbo(Class clazz, String configName) {
            DubboConfDO dubboConf = ConfigFactory.getDubboConf(configName);
            // 引用远程服务
            // 服务实现,对应配置中的 <dubbo:reference id="dubooTestDemo" group="jd" version="1.0" timeout="2000" interface="com.jd.service.DubboTestService"/>
            // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
            ReferenceConfig reference = new ReferenceConfig();
            reference.setApplication(application);
            // 多个注册中心可以用setRegistries()
            if ("zookeeper".equalsIgnoreCase(dubboConf.getProtocol())) {
                RegistryConfig registry = new RegistryConfig();
                registry.setCheck(false);
                registry.setRegister(false);
                registry.setAddress(dubboConf.getAddress());
                registry.setClient("curator");
                registry.setProtocol(dubboConf.getProtocol());
                registry.setGroup(dubboConf.getGroup());
                application.setRegistry(registry);
            } else {
                reference.setProtocol(dubboConf.getProtocol());
                reference.setUrl(dubboConf.getAddress());
                reference.setGroup(dubboConf.getGroup());
            }
            reference.setInterface(clazz);
            reference.setGroup(dubboConf.getGroup());
            reference.setTimeout(10000);
            reference.setMethods(new ArrayList<>(0));
            REFERENCE_CONFIG_MAP.put(clazz, reference);
        }

        private void intMethod(Class clazz, String methodName) {
            //方法的设置
            MethodConfig method = new MethodConfig();
            method.setName(methodName);
            method.setTimeout(10000);
            //方法重试
            method.setRetries(0);
            REFERENCE_CONFIG_MAP.get(clazz).getMethods().add(method);
            Map<String, MethodConfig> configMap = METHOD_CONFIG_MAP.get(clazz);
            if (configMap == null) {
                configMap = new HashMap<>();
                METHOD_CONFIG_MAP.put(clazz, configMap);
            }
            configMap.put(methodName, method);
        }

        private ReferenceConfig getReferenceConfig(Class clazz, String configName) {
            ReferenceConfig referenceConfig = REFERENCE_CONFIG_MAP.get(clazz);
            if (referenceConfig == null) {
                synchronized (REFERENCE_CONFIG_MAP) {
                    if (REFERENCE_CONFIG_MAP.containsKey(clazz)) {
                        return REFERENCE_CONFIG_MAP.get(clazz);
                    }
                    initDubbo(clazz, configName);
                    referenceConfig = REFERENCE_CONFIG_MAP.get(clazz);
                }
            }
            return referenceConfig;
        }

        private MethodConfig getMethodConfig(Class clazz, String methodName) {
            Map<String, MethodConfig> methodConfig = METHOD_CONFIG_MAP.get(clazz);
            if (methodConfig == null || !methodConfig.containsKey(methodName)) {
                synchronized (METHOD_CONFIG_MAP) {
                    if (METHOD_CONFIG_MAP.containsKey(clazz)) {
                        return METHOD_CONFIG_MAP.get(clazz).get(methodName);
                    }
                    intMethod(clazz, methodName);
                    methodConfig = METHOD_CONFIG_MAP.get(clazz);
                }
            }
            return methodConfig.get(methodName);
        }

        private MethodConfig getMethodConfig(Class clazz, String methodName, String configName) {
            getReferenceConfig(clazz, configName);
            return getMethodConfig(clazz, methodName);
        }

        private Object getServiceObject(Class clazz, String methodName, String configName) {
            Object o = SERVICE_CLASS_MAP.get(clazz);
            if (o == null) {
                synchronized (SERVICE_CLASS_MAP) {
                    if (SERVICE_CLASS_MAP.containsKey(clazz)) {
                        return SERVICE_CLASS_MAP.get(clazz);
                    }
                    ReferenceConfig referenceConfig = getReferenceConfig(clazz, configName);
                    getMethodConfig(clazz, methodName, configName);
                    o = referenceConfig.get();
                    SERVICE_CLASS_MAP.put(clazz, o);
                }
            }
            return o;
        }

        public void unload() {
            REFERENCE_CONFIG_MAP.values().forEach(r -> {
                if (r != null) {
                    try {
                        r.destroy();
                    } catch (Throwable e) {
                        logger.warn("卸载REFERENCE_CONFIG出错", e);
                    }
                }
            });
            METHOD_CONFIG_MAP.clear();
            SERVICE_CLASS_MAP.clear();
        }
    }


    private DubboApplication getApplicationConfig(String name) {
        DubboApplication dubboApplication = APPLICATION_CONFIG_MAP.get(name);
        if (dubboApplication == null) {
            synchronized (APPLICATION_CONFIG_MAP) {
                if (!APPLICATION_CONFIG_MAP.containsKey(name)) {
                    dubboApplication = new DubboApplication(name);
                    APPLICATION_CONFIG_MAP.put(name, dubboApplication);
                }
            }
        }
        return APPLICATION_CONFIG_MAP.get(name);
    }


    @Override
    public Object run(Method method, Map<String, Object> context, Map params) {
        DubboConfDO dubboConf = ConfigFactory.getCurrentDubboConf();
        if (dubboConf == null) {
            JOptionPane.showMessageDialog(null, "请先配置Dubbo连接", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        ApiContext runtimeApi = RuntimeApiContext.getRuntimeApi();
        String className = runtimeApi.getApiGroupModel().getClassName();
        ClassLoader classLoader = RuntimeApiContext.getClassLoader();
        Class serviceClass = null;
        try {
            serviceClass = Class.forName(className, false, classLoader);
            String methodName = runtimeApi.getApiModel().getId();
            Object service = getApplicationConfig(dubboConf.getId())
                    .getServiceObject(serviceClass, methodName, dubboConf.getId());
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (params.size() != parameterTypes.length) {
                throw new IllegalArgumentException(
                        "参数个数不正确params.size()=" + params.size()
                                + ",parameterTypes.length=" + parameterTypes.length);
            }
            Method targetMethod = service.getClass()
                    .getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(service, params.values().toArray());
        } catch (Throwable e) {
            logger.error("dubbo执行出错：", e);
        }
        return null;
    }

    @Override
    public void unload(String appName) {
        DubboApplication remove = APPLICATION_CONFIG_MAP.remove(appName);
        if (remove != null) {
            remove.unload();
        }
    }
}
