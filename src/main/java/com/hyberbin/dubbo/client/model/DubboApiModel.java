package com.hyberbin.dubbo.client.model;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DubboApiModel extends ApiModel {

    private Method method;
    private Class clazz;
}
