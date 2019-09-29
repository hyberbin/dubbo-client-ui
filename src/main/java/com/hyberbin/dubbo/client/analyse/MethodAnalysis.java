package com.hyberbin.dubbo.client.analyse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MethodAnalysis {

    private transient Method method;
    private String methodName;
    private List<BeanProperty> properties = new ArrayList();
    private Map<String, String> parameterStringMap = new LinkedHashMap<>();
    private Map<String, Object> parameterMap = new LinkedHashMap<>();

    public MethodAnalysis() {
    }

    public void addBeanProperty(BeanProperty beanProperty) {
        this.properties.add(beanProperty);
    }

    public Object[] getParameters() {
        Object[] parameters = new Object[properties.size()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = properties.get(i).getObjectValue();
            parameterStringMap.put(properties.get(i).getName(), properties.get(i).getValue());
            parameterMap.put(properties.get(i).getName(), parameters[i]);
        }
        return parameters;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.properties.forEach((e) -> {
            sb.append(e.toString());
        });
        return sb.toString();
    }
}
