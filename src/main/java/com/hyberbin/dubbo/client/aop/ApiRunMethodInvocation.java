package com.hyberbin.dubbo.client.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hyberbin.dubbo.client.config.RuntimeApiContext;
import com.hyberbin.dubbo.client.config.RuntimeApiContext.ApiContext;
import com.hyberbin.dubbo.client.dynamic.DynamicEngine;
import com.hyberbin.dubbo.client.exception.DubboClientUException;
import com.hyberbin.dubbo.client.model.TestCase;
import com.hyberbin.dubbo.client.runner.Interceptor;
import com.hyberbin.dubbo.client.utils.VelocityUtils;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiRunMethodInvocation implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiRunMethodInvocation.class);

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getMethod().getName().equals("run")) {
            ApiContext runtimeApi = RuntimeApiContext.getRuntimeApi();
            URLClassLoader classLoader = RuntimeApiContext.getClassLoader();
            TestCase testCase = runtimeApi.getTestCase();
            if (StringUtils.isNotBlank(testCase.getTestCaseDO().getGroovyScripts())) {
                try {
                    Object object = DynamicEngine.getInstance()
                            .javaCodeToObject("ApiInterceptor",
                                    testCase.getTestCaseDO().getGroovyScripts(),
                                    classLoader);
                    if (object instanceof Interceptor) {
                        Interceptor interceptor = (Interceptor) object;
                        Object[] arguments = methodInvocation.getArguments();
                        mergeByTemplate((Map) arguments[1], object, runtimeApi);
                        mergeByTemplate((Map) arguments[2], object, runtimeApi);
                        interceptor.before((Map) arguments[1], (Map) arguments[2]);
                        Object proceed = methodInvocation.proceed();
                        return interceptor.after(proceed);
                    } else {
                        throw new DubboClientUException(
                                "类名必须是ApiInterceptor且是com.hyberbin.dubbo.client.runner.Interceptor的子类");
                    }
                } catch (Throwable e) {
                    logger.warn("执行动态脚本出错！直接运行", e);
                    return methodInvocation.proceed();
                }
            }
        }
        return methodInvocation.proceed();
    }

    private void mergeByTemplate(Map parms, Object object, ApiContext runtimeApi) {
        //将变量参数替换
        String jsonString = JSON.toJSONString(parms, SerializerFeature.WriteClassName);
        Map vars = new HashMap(parms);
        vars.putAll(RuntimeApiContext.getVars());
        vars.put("script", object);
        jsonString = VelocityUtils.evaluate(jsonString, vars);
        Map newMap = JSON.parseObject(jsonString, LinkedHashMap.class);
        parms.clear();
        parms.putAll(newMap);
        //变量参数替换结束
    }
}
