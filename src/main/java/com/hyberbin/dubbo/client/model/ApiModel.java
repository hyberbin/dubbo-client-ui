package com.hyberbin.dubbo.client.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ApiModel {

    private String id = "";
    private String name = "";
    private List<ApiParamModel> apiParams = new ArrayList<>();
    private ApiReturnDataModel returnData;

    public void addApiParams(ApiParamModel apiParam) {
        apiParams.add(apiParam);
    }
}
