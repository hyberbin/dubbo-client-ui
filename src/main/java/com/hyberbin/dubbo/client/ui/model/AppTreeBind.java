package com.hyberbin.dubbo.client.ui.model;

import com.hyberbin.dubbo.client.config.AppConfig;
import com.hyberbin.dubbo.client.model.AppModel;
import com.hyberbin.dubbo.client.vo.AppModeVO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppTreeBind {

    private AppModeVO model;
    private boolean initialized;

    public AppTreeBind(AppModeVO model) {
        this.model = model;
    }

    public AppModel getModel() {
        return model.getAppModel();
    }

    public AppModeVO getAppModeVO() {
        return model;
    }

    public AppConfig getAppConfig() {
        return model.getAppConfig();
    }

    @Override
    public String toString() {
        return getModel().getAppName();
    }
}
