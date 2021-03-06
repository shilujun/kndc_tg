package com.cashhub.cash.app.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.app.model.ReportInfo;

import com.cashhub.cash.app.greendao.ConfigDao;
import com.cashhub.cash.app.greendao.ReportInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig configDaoConfig;
    private final DaoConfig reportInfoDaoConfig;

    private final ConfigDao configDao;
    private final ReportInfoDao reportInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        configDaoConfig = daoConfigMap.get(ConfigDao.class).clone();
        configDaoConfig.initIdentityScope(type);

        reportInfoDaoConfig = daoConfigMap.get(ReportInfoDao.class).clone();
        reportInfoDaoConfig.initIdentityScope(type);

        configDao = new ConfigDao(configDaoConfig, this);
        reportInfoDao = new ReportInfoDao(reportInfoDaoConfig, this);

        registerDao(Config.class, configDao);
        registerDao(ReportInfo.class, reportInfoDao);
    }
    
    public void clear() {
        configDaoConfig.clearIdentityScope();
        reportInfoDaoConfig.clearIdentityScope();
    }

    public ConfigDao getConfigDao() {
        return configDao;
    }

    public ReportInfoDao getReportInfoDao() {
        return reportInfoDao;
    }

}
