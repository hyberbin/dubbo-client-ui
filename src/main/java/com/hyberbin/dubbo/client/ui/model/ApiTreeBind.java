package com.hyberbin.dubbo.client.ui.model;

import com.hyberbin.dubbo.client.model.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiTreeBind {

    private ApiModel apiModel;
    private ApiGroupTreeBind apiGroupTreeBind;

    @Override
    public String toString() {
        return apiModel.getName();
    }
}
