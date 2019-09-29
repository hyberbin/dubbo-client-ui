package com.hyberbin.dubbo.client.enums;

public enum TestCaseType {
    DUBBO, HTTP;

    public static String[] ALL_ENVS = new String[]{"DUBBO", "HTTP"};

    public static TestCaseType getTestCaseType(String type) {
        for (TestCaseType envType : TestCaseType.values()) {
            if (envType.name().equals(type)) {
                return envType;
            }
        }
        return null;
    }
}
