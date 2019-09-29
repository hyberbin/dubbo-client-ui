package com.hyberbin.dubbo.client.model;

import com.hyberbin.dubbo.client.domain.TestCaseDO;
import com.hyberbin.dubbo.client.domain.TestCaseKVDO;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class TestCase {

    private TestCaseDO testCaseDO;
    private List<TestCaseKVDO> caseValues = new ArrayList<>();
    private Object[] parameters;
    private Map<String, String> caseValueMap = new LinkedHashMap<>();
    private Map<String, Object> caseObjectMap = new LinkedHashMap<>();
    private Method method;
}
