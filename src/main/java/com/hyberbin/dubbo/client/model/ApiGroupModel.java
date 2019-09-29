package com.hyberbin.dubbo.client.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ApiGroupModel {

    private String value;
    private String className;

    private List<ApiModel> apiList=new ArrayList<>();

    public void addApi(ApiModel api){
        apiList.add(api);
    }

}
