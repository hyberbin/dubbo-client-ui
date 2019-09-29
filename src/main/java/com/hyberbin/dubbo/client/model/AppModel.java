package com.hyberbin.dubbo.client.model;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppModel {

    /**
     * 源包
     */
    private File sourceFile;
    /**
     * jar包装载器
     */
    private URLClassLoader classLoader;
    /**
     * 应用名称
     */
    private String appName;

    /**
     * pom的groupId
     */
    private String groupId;

    /**
     * pom的artifactId
     */
    private String artifactId;

    /**
     * pom的version
     */
    private String version;

    /**
     * 应用描述
     */
    private String desc;
    /**
     * 类集合
     */
    private List<ApiGroupModel> apiGroupList = new ArrayList<>();

    public void addApiGroup(ApiGroupModel apiGroup) {
        apiGroupList.add(apiGroup);
    }

    public AppModel(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.appName = artifactId;
    }
}
