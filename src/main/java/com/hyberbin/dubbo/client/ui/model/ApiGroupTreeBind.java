package com.hyberbin.dubbo.client.ui.model;

import com.hyberbin.dubbo.client.model.ApiGroupModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiGroupTreeBind {

    private ApiGroupModel apiGroupModel;
    private AppTreeBind appTreeBind;


    @Override
    public String toString() {
        return apiGroupModel.getValue();
    }
}
