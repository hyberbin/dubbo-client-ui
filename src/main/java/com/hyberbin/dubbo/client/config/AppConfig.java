package com.hyberbin.dubbo.client.config;

import com.hyberbin.dubbo.client.model.DependencyModel;
import lombok.Data;

@Data
public class AppConfig {

    /**
     * 主依赖项
     */
    private DependencyModel mainDependency;
    private DependencyModel[] extDependencies;
}
