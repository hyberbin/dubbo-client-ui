package com.hyberbin.dubbo.client.domain;

import lombok.Data;

@Data
public class TestCaseDO {

    private Integer id;
    private String caseName;
    private String className;
    private String methodName;
    private String url = "";
    private String groovyScripts = "";
}
