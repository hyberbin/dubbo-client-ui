package com.hyberbin.dubbo.client.config;

import com.hyberbin.dubbo.client.model.ApiGroupModel;
import com.hyberbin.dubbo.client.model.ApiModel;
import com.hyberbin.dubbo.client.model.TestCase;
import com.hyberbin.dubbo.client.ui.model.ApiTreeBind;
import com.hyberbin.dubbo.client.utils.AppUtils;
import com.hyberbin.dubbo.client.vo.AppModeVO;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RuntimeApiContext {

    private static final ThreadLocal<RuntimeApiContext> RUNTIME_API_THREAD_LOCAL = new ThreadLocal<>();

    private Stack<ApiContext> apiContextStack = new Stack<>();
    private Map runtimeVars = new HashMap();
    private URLClassLoader classLoader;

    public static ApiContext getRuntimeApi() {
        RuntimeApiContext runtimeApiContext = RUNTIME_API_THREAD_LOCAL.get();
        return runtimeApiContext.getApiContextStack().peek();
    }

    public static void setRuntimeApiModel(ApiContext api) {
        RuntimeApiContext runtimeApiContext = RUNTIME_API_THREAD_LOCAL.get();
        if (runtimeApiContext == null) {
            runtimeApiContext = new RuntimeApiContext();
            runtimeApiContext.apiContextStack.push(api);
            RUNTIME_API_THREAD_LOCAL.set(runtimeApiContext);
            runtimeApiContext.classLoader = (URLClassLoader) AppUtils
                    .getAppClassLoaderByCache(api.getAppModeVO());
        } else {
            runtimeApiContext.apiContextStack.push(api);
        }
    }

    public static void removeRuntimeApiModel() {
        RuntimeApiContext runtimeApiContext = RUNTIME_API_THREAD_LOCAL.get();
        runtimeApiContext.apiContextStack.pop();
        if (runtimeApiContext.apiContextStack.size() == 0) {
            RUNTIME_API_THREAD_LOCAL.remove();
        }
    }

    public static void putVar(Object key, Object value) {
        RUNTIME_API_THREAD_LOCAL.get().runtimeVars.put(key, value);
    }

    public static URLClassLoader getClassLoader() {
        return RUNTIME_API_THREAD_LOCAL.get().classLoader;
    }

    public static Map getVars() {
        return RUNTIME_API_THREAD_LOCAL.get().runtimeVars;
    }

    @Data
    @AllArgsConstructor
    public static class ApiContext {

        private ApiTreeBind apiTreeBind;
        private TestCase testCase;

        public AppConfig getAppConfig() {
            return apiTreeBind.getApiGroupTreeBind().getAppTreeBind().getAppConfig();
        }

        public AppModeVO getAppModeVO() {
            return apiTreeBind.getApiGroupTreeBind().getAppTreeBind().getAppModeVO();
        }

        public ApiModel getApiModel() {
            return apiTreeBind.getApiModel();
        }

        public ApiGroupModel getApiGroupModel() {
            return apiTreeBind.getApiGroupTreeBind().getApiGroupModel();
        }

        public TestCase getTestCase() {
            return testCase;
        }
    }
}
