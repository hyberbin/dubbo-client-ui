package com.hyberbin.dubbo.client.vo;

import com.hyberbin.dubbo.client.config.AppConfig;
import com.hyberbin.dubbo.client.model.AppModel;
import java.io.File;
import lombok.Data;

@Data
public class AppModeVO {

    private String id;
    private AppModel appModel;
    private AppConfig appConfig;


    public AppModeVO() {
    }

    public AppModeVO(AppModel appModel, AppConfig appConfig) {
        this.appModel = appModel;
        this.appConfig = appConfig;
    }

    public String getWorkSpace() {
        String artifactId =
                appModel.getArtifactId() == null ? appModel.getAppName() : appModel.getArtifactId();
        return new File("apps/" + artifactId + "/"+id+"/").getAbsolutePath();
    }
}
