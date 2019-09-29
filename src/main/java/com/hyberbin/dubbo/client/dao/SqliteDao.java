package com.hyberbin.dubbo.client.dao;

import com.alibaba.fastjson.JSON;
import com.hyberbin.dubbo.client.config.ConfigFactory;
import com.hyberbin.dubbo.client.domain.AppDO;
import com.hyberbin.dubbo.client.domain.DubboConfDO;
import com.hyberbin.dubbo.client.domain.TestCaseDO;
import com.hyberbin.dubbo.client.domain.TestCaseKVDO;
import com.hyberbin.dubbo.client.exception.DubboClientUException;
import com.hyberbin.dubbo.client.exception.SystemException;
import com.hyberbin.dubbo.client.model.TestCase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jplus.hyb.database.crud.DatabaseAccess;
import org.jplus.hyb.database.crud.Hyberbin;
import org.jplus.hyb.database.sqlite.SqliteUtil;
import org.jplus.hyb.database.transaction.IDbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteErrorCode;

public class SqliteDao {

    private static final Logger logger = LoggerFactory.getLogger(SqliteDao.class);

    public SqliteDao() {
        dbSetup();
    }

    public void dbSetup() {
        if (!SqliteUtil.tableExist("AppDO")) {
            SqliteUtil.execute(
                    "create table AppDO(id text primary key,appName text unique,config text,testCase text);");
        }
        if (!SqliteUtil.tableExist("DubboConfDO")) {
            SqliteUtil.execute(
                    "create table DubboConfDO(id text primary key,protocol text,address text,`group` text);");
        }
        if (!SqliteUtil.tableExist("TestCaseDO")) {
            SqliteUtil.execute(
                    "create table TestCaseDO(id INTEGER primary key AUTOINCREMENT,caseName text,className text,methodName text,url text,groovyScripts text,unique(className,methodName,caseName));");
        }
        if (!SqliteUtil.tableExist("TestCaseKVDO")) {
            SqliteUtil.execute(
                    "create table TestCaseKVDO(id INTEGER primary key AUTOINCREMENT,caseId int,key text,value text);");
        }
    }

    public List<AppDO> getAllApp() {
        Hyberbin hyberbin = new Hyberbin(new AppDO(), ConfigFactory.getSimpleManage());
        try {
            return hyberbin.showAll();
        } catch (SQLException e) {
            logger.error("getAllApp error!", e);
            return Collections.EMPTY_LIST;
        }
    }

    public AppDO getApp(String name) {
        AppDO appDO = new AppDO();
        appDO.setAppName(name);
        Hyberbin<AppDO> hyberbin = new Hyberbin(appDO, ConfigFactory.getSimpleManage());
        try {
            AppDO app = hyberbin.showOnebyKey("appName");
            return app.getId()==null?null:app;
        } catch (SQLException e) {
            logger.error("getAllApp error!", e);
            return null;
        }
    }

    public List<DubboConfDO> getAllDubboConf() {
        Hyberbin hyberbin = new Hyberbin(new DubboConfDO(), ConfigFactory.getSimpleManage());
        try {
            return hyberbin.showAll();
        } catch (SQLException e) {
            logger.error("getAllDubboConf error!", e);
            return Collections.EMPTY_LIST;
        }
    }

    public void saveUserLog() {
    }

    public void saveApp(AppDO appDO) {
        Hyberbin hyberbin = new Hyberbin(appDO, ConfigFactory.getSimpleManage());
        try {
            if (StringUtils.isNotBlank(appDO.getId())) {
                hyberbin.updateByKey("id");
            } else {
                appDO.setId(UUID.randomUUID().toString().replace("-", ""));
                hyberbin.insert("");
            }
        } catch (SQLException e) {
            if (SQLiteErrorCode.SQLITE_CONSTRAINT.code == e.getErrorCode()) {
                throw new DubboClientUException("已经有同名应用存在", e);
            }
            throw new SystemException("saveApp error!", e);
        }
    }

    public String updateAppId(String id) {
        DatabaseAccess databaseAccess = new DatabaseAccess(ConfigFactory.getSimpleManage());
        try {
            String newId=UUID.randomUUID().toString().replace("-", "");
            databaseAccess.update("update AppDO set id='"+newId+"' where id='"+id+"'");
            return newId;
        } catch (SQLException e) {
            throw new SystemException("updateAppId error!", e);
        }
    }

    public void saveDubboConf(DubboConfDO dubboConfDO) {
        Hyberbin hyberbin = new Hyberbin(dubboConfDO, ConfigFactory.getSimpleManage());
        try {
            DubboConfDO old =new DubboConfDO();
            old.setId(dubboConfDO.getId());
            new Hyberbin<>(old, ConfigFactory.getSimpleManage()).showOnebyKey("id");
            if (old.getAddress()!=null) {
                hyberbin.updateByKey("id");
            } else {
                hyberbin.insert("");
            }
        } catch (SQLException e) {
            if (SQLiteErrorCode.SQLITE_CONSTRAINT.code == e.getErrorCode()) {
                throw new DubboClientUException("已经有同名配置存在", e);
            }
            throw new SystemException("saveDubboConf error!", e);
        }
    }

    @SneakyThrows
    public void deleteDubboConf(String id){
        DubboConfDO dubboConfDO=new DubboConfDO();
        dubboConfDO.setId(id);
        Hyberbin hyberbin = new Hyberbin(dubboConfDO,
                ConfigFactory.getSimpleManage());
        hyberbin.deleteByKey("id");
    }

    public int deleteApp(String id) {
        AppDO appDO = new AppDO();
        appDO.setId(id);
        Hyberbin hyberbin = new Hyberbin(appDO, ConfigFactory.getSimpleManage());
        try {
            return hyberbin.deleteByKey("id");
        } catch (SQLException e) {
            logger.error("deleteApp error!,id:{}", id, e);
        }
        return 0;
    }

    @SneakyThrows
    public void saveTestCase(TestCase testCase) {
        IDbManager manager = ConfigFactory.getTxManage();
        try {
            TestCaseDO testCaseDO = testCase.getTestCaseDO();
            saveTestCaseDO(testCaseDO, manager);
            List<TestCaseKVDO> caseValues = testCase.getCaseValues();
            if (!CollectionUtils.isEmpty(caseValues)) {
                new Hyberbin<TestCaseKVDO>(new TestCaseKVDO(), manager).delete(
                        " where caseId=" + testCase.getTestCaseDO().getId());
                for (TestCaseKVDO caseKVDO : caseValues) {
                    caseKVDO.setId(null);
                    caseKVDO.setCaseId(testCaseDO.getId());
                    saveTestCaseKVDO(caseKVDO, manager);
                }
            }
        } catch (SQLException e) {
            logger.error("saveTestCaseDO error!", e);
        } finally {
            manager.finalCloseConnection();
        }
    }

    private void saveTestCaseDO(TestCaseDO testCaseDO, IDbManager manager) {
        try {
            Hyberbin hyberbin = new Hyberbin(testCaseDO, manager);
            if (testCaseDO.getId() != null) {
                hyberbin.updateByKey("id");
            } else {
                hyberbin.insert("id");
                TestCaseDO select = new Hyberbin<TestCaseDO>(testCaseDO, manager)
                        .showOne(
                                "select * from TestCaseDO where caseName=? and className=? and methodName=?",
                                testCaseDO.getCaseName(), testCaseDO.getClassName(),
                                testCaseDO.getMethodName());
                testCaseDO.setId(select.getId());
            }
        } catch (SQLException e) {
            logger.error("saveTestCaseDO error!", e);
        }
    }

    private void saveTestCaseKVDO(TestCaseKVDO testCaseKVDO, IDbManager manager) {
        try {
            Hyberbin hyberbin = new Hyberbin(testCaseKVDO, manager);
            if (testCaseKVDO.getId() != null) {
                hyberbin.updateByKey("id");
            } else {
                hyberbin.insert("id");
                TestCaseKVDO select = new Hyberbin<TestCaseKVDO>(testCaseKVDO, manager)
                        .showOne("select * from TestCaseKVDO where caseId=? and key=? ",
                                testCaseKVDO.getCaseId(), testCaseKVDO.getKey());
                testCaseKVDO.setId(select.getId());
            }
        } catch (SQLException e) {
            logger.error("saveTestCaseKVDO error!", e);
        }
    }

    public void deleteTestCaseDO(TestCase testCase) {
        try {
            Hyberbin hyberbin = new Hyberbin(testCase.getTestCaseDO(),
                    ConfigFactory.getSimpleManage());
            hyberbin.deleteByKey("id");
            new Hyberbin(new TestCaseKVDO(), ConfigFactory.getSimpleManage()).delete(
                    " where caseId=" + testCase.getTestCaseDO().getId());
        } catch (SQLException e) {
            logger.error("deleteTestCaseDO error!,testCase:{}", JSON.toJSONString(testCase), e);
        }
    }

    public List<TestCase> getTestCaseForMethod(String className, String methodName) {
        List<TestCase> testCaseList = new ArrayList<>();
        try {

            Hyberbin hyberbin = new Hyberbin(new TestCaseDO(), ConfigFactory.getSimpleManage());
            List<TestCaseDO> testCaseDOList = hyberbin
                    .showList("select * from TestCaseDO where className=? and methodName=?",
                            className,
                            methodName);
            for (TestCaseDO testCaseDO : testCaseDOList) {
                hyberbin = new Hyberbin(new TestCaseKVDO(), ConfigFactory.getSimpleManage());
                List<TestCaseKVDO> list = hyberbin
                        .showList("select * from TestCaseKVDO where caseId=? ", testCaseDO.getId());
                TestCase testCase = new TestCase();
                testCase.setCaseValues(list);
                testCase.setTestCaseDO(testCaseDO);
                testCaseList.add(testCase);
            }
        } catch (Exception e) {
            logger.error("getTestCaseForMethod error!", e);
        }
        return testCaseList;
    }
}
