package com.hyberbin.dubbo.client.model;

import java.lang.reflect.Parameter;
import lombok.Data;

@Data
public class ParameterWrapper {

    private Parameter parameter;
    private String realName;
}
