package com.hyberbin.dubbo.client.runner;

import java.util.Map;

public interface Interceptor {

    void before(Map<String, Object> context, Map args);

    Object after(Object o);
}
