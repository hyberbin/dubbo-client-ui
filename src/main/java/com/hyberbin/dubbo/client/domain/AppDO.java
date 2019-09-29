package com.hyberbin.dubbo.client.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.hyberbin.dubbo.client.config.AppConfig;
import com.hyberbin.dubbo.client.model.AppModel;
import com.hyberbin.dubbo.client.model.DubboApiModel;
import com.hyberbin.dubbo.client.vo.AppModeVO;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;

@Data
public class AppDO {

    private String id;
    private String appName;
    private String config;

    public AppDO() {
    }

    @SneakyThrows
    public AppDO(AppModeVO appModeVO) {
        AppModel appModelClone = new AppModel();
        BeanUtils.copyProperties(appModelClone, appModeVO.getAppModel());
        appModelClone.setClassLoader(null);
        appModelClone.setSourceFile(null);
        this.setAppName(appModelClone.getAppName());
        appModelClone.getApiGroupList().forEach(g->{
            g.getApiList().forEach(a->{
                if(a instanceof DubboApiModel){
                    ((DubboApiModel) a).setClazz(null);
                    ((DubboApiModel) a).setMethod(null);
                }
            });
        });
        this.config = JSON.toJSONString(appModeVO.getAppConfig());
        this.id = appModeVO.getId();
    }

    @JSONField(serialize = false, deserialize = false)
    public AppModeVO getAppModeVO() {
        AppModeVO appModeVO = new AppModeVO();
        appModeVO.setAppConfig(JSON.parseObject(config, AppConfig.class));
        appModeVO.setAppModel(new AppModel());
        appModeVO.setId(id);
        appModeVO.getAppModel().setAppName(appName);
        appModeVO.getAppModel().setArtifactId(appName);
        return appModeVO;
    }
}
