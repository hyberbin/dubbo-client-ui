package com.hyberbin.dubbo.client.ui.model;

import com.hyberbin.dubbo.client.model.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestCaseTreeBind {

    private TestCase testCase;
    private ApiTreeBind apiTreeBind;

    @Override
    public String toString() {
        return testCase.getTestCaseDO().getCaseName();
    }
}
