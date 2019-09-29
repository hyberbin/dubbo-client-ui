package com.hyberbin.dubbo.client.runner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface ApiRunner {

    String CONTEXT_KEY_HTTP_TIMEOUT = "timeout";
    String CONTEXT_KEY_HTTP_REQUESTHEADER = "requestHeader";
    String CONTEXT_KEY_HTTP_URL = "url";
    String CONTEXT_KEY_HTTP_METHOD = "method";
    String CONTEXT_KEY_RETURN_TYPE = "returnType";

    String CONTEXT_KEY_DUBBO_INTERFACE = "dubboInterface";
    String CONTEXT_KEY_DUBBO_GROUP = "dubboGroup";
    String CONTEXT_KEY_DUBBO_VERSION = "dubboVersion";

    static Map<String, Object> buildHttpContext(String url, Class returnType) {
        Map<String, Object> context = new HashMap<>();
        context.put(CONTEXT_KEY_HTTP_URL, url);
        context.put(CONTEXT_KEY_RETURN_TYPE, returnType);
        context.put(CONTEXT_KEY_HTTP_METHOD, "post");
        return context;
    }

    Object run(Method method, Map<String, Object> context, Map params);

    /**
     * 卸载某个应用的运行器
     */
    void unload(String app);
}