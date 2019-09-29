package com.hyberbin.dubbo.client.analyse;

import com.hyberbin.dubbo.client.model.ParameterWrapper;
import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class JavaMethodAnalyser {

    @Inject
    private JavaParameterAnalyser javaParameterAnalyser;
    @Inject
    private JavaSourceAnalyser javaSourceAnalyser;

    private static final Map<Method, MethodAnalysis> METHOD_ANALYSIS_MAP = new ConcurrentHashMap<>();

    public JavaMethodAnalyser() {
    }

    public MethodAnalysis analyse(Class clazz, Method targetMethod) {
        MethodAnalysis analysis = METHOD_ANALYSIS_MAP.get(targetMethod);
        if (analysis == null) {
            synchronized (METHOD_ANALYSIS_MAP) {
                if (!METHOD_ANALYSIS_MAP.containsKey(targetMethod)) {
                    Parameter[] parameters = targetMethod.getParameters();
                    MethodAnalysis methodAnalysis = new MethodAnalysis();
                    methodAnalysis.setMethod(targetMethod);
                    ParameterWrapper[] parameterWarps = javaSourceAnalyser
                            .getParameter(parameters, targetMethod);
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        BeanProperty property = javaParameterAnalyser
                                .analyse(clazz, targetMethod, parameter);
                        String realName = parameterWarps[i].getRealName();
                        if (clazz.isInterface() && StringUtils.isNotBlank(realName)) {
                            property.setName(realName);
                        }
                        methodAnalysis.addBeanProperty(property);
                        METHOD_ANALYSIS_MAP.put(targetMethod, methodAnalysis);
                    }
                }
            }
        }
        log.info("开始分析调用方法，方法为{}", targetMethod.toString());
        return METHOD_ANALYSIS_MAP.get(targetMethod);
    }
}
