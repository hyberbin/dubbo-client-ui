package com.hyberbin.dubbo.client.config;

import com.hyberbin.dubbo.client.dao.SqliteDao;
import com.hyberbin.dubbo.client.domain.DubboConfDO;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jplus.hyb.database.config.DbConfig;
import org.jplus.hyb.database.transaction.IDbManager;
import org.jplus.hyb.database.transaction.SimpleManager;
import org.jplus.hyb.database.transaction.SingleManager;

public class ConfigFactory {

    private static final DbConfig DEFAULT_CONFIG = new DbConfig(DbConfig.URL_SQLITE, "sqlite");
    private static final Map<String, DubboConfDO> DUBBO_CONF_MAP = new LinkedHashMap<>();
    private static String currentDubboConf;
    public static final String DEFAULT_ITEM_TEXT="---编辑配置---";


    public static DbConfig getDbConfig() {
        return DEFAULT_CONFIG;
    }


    public static IDbManager getSimpleManage() {
        return new SimpleManager(getDbConfig().getConfigName());
    }

    public static IDbManager getTxManage() {
        return new SingleManager(getDbConfig().getConfigName());
    }

    public static void loadDubboConf() {
        SqliteDao sqliteDao = CoderQueenModule.getInstance(SqliteDao.class);
        DUBBO_CONF_MAP.clear();
        List<DubboConfDO> allDubboConf = sqliteDao.getAllDubboConf();
        allDubboConf.forEach(conf -> {
            DUBBO_CONF_MAP.put(conf.getId(), conf);
        });
    }

    public static DubboConfDO getDubboConf(String name) {
        return DUBBO_CONF_MAP.get(name);
    }

    public static Collection<DubboConfDO> getDubboConfs() {
        return DUBBO_CONF_MAP.values();
    }

    public static DubboConfDO getCurrentDubboConf() {
        return currentDubboConf == null ? null : getDubboConf(currentDubboConf);
    }

    public static void setCurrentDubboConf(String currentDubboConf) {
        ConfigFactory.currentDubboConf = currentDubboConf;
    }
}
