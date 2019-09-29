package com.hyberbin.dubbo.client.utils;

import com.hyberbin.dubbo.client.config.CoderQueenModule;
import com.hyberbin.dubbo.client.config.RuntimeApiContext;
import com.hyberbin.dubbo.client.config.RuntimeApiContext.ApiContext;
import com.hyberbin.dubbo.client.enums.TestCaseType;
import com.hyberbin.dubbo.client.model.TestCase;
import com.hyberbin.dubbo.client.runner.ApiRunner;
import com.hyberbin.dubbo.client.ui.frames.DubboUIFrame;
import com.hyberbin.dubbo.client.ui.model.ApiTreeBind;
import com.hyberbin.dubbo.client.ui.model.TestCaseTreeBind;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;

public class TestCaseUtils {

    @SneakyThrows
    public static Object runTestCase(TestCase testCase, ApiTreeBind apiTreeBind,
            TestCaseType testCaseType) {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = AppUtils.getAppClassLoaderByCache(
                apiTreeBind.getApiGroupTreeBind().getAppTreeBind().getAppModeVO());
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            ApiContext runtimeApiContext = new ApiContext(apiTreeBind, testCase);
            RuntimeApiContext.setRuntimeApiModel(runtimeApiContext);
            String runnerName = !"DUBBO".contains(testCaseType.name()) ? "httpPostRunner"
                    : testCaseType.name() + "Runner";
            ApiRunner apiRunner = CoderQueenModule.getInstance(ApiRunner.class, runnerName);
            Map<String, Object> map = ApiRunner
                    .buildHttpContext(testCase.getTestCaseDO().getUrl(), HashMap.class);
            Object runResult = apiRunner.run(testCase.getMethod(),map, testCase.getCaseObjectMap());
            return runResult;
        } finally {
            RuntimeApiContext.removeRuntimeApiModel();
            Thread.currentThread().setContextClassLoader(old);
        }
    }

    public static Object runTestCase(String appName, String appGroupName, String apiName,
            String testCaseName, TestCaseType testCaseType) {
        DubboUIFrame frame = CoderQueenModule.getInstance(DubboUIFrame.class);
        ApiTreeBind apiTreeBind = frame.getApiTreeBind(appName, appGroupName, apiName);
        TestCaseTreeBind testCaseTreeBind = frame
                .getTestCaseTreeBind(appName, appGroupName, apiName, testCaseName);
        return runTestCase(testCaseTreeBind.getTestCase(), apiTreeBind, testCaseType);
    }
}
