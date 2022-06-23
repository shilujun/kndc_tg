package com.cashhub.cash.app.db;

import com.cashhub.cash.app.greendao.ConfigDao;
import com.cashhub.cash.app.greendao.ReportInfoDao;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.app.model.ReportInfo;

/**
 * 初始化、存放及获取DaoUtils
 */
public class DaoUtilsStore {
  private volatile static DaoUtilsStore instance = new DaoUtilsStore();
  private CommonDaoUtils<Config> mConfigDaoUtils;
  private CommonDaoUtils<ReportInfo> mReportDaoUtils;

  public static DaoUtilsStore getInstance() {
    return instance;
  }

  private DaoUtilsStore() {
    DaoManager mManager = DaoManager.getInstance();

    ConfigDao _ConfigDao = mManager.getDaoSession().getConfigDao();
    mConfigDaoUtils = new CommonDaoUtils<>(Config.class, _ConfigDao);


    ReportInfoDao _ReportDao = mManager.getDaoSession().getReportInfoDao();
    mReportDaoUtils = new CommonDaoUtils<>(ReportInfo.class, _ReportDao);
  }

  public CommonDaoUtils<Config> getConfigDaoUtils() {
    return mConfigDaoUtils;
  }

  public CommonDaoUtils<ReportInfo> getReportInfoDaoUtils() {
    return mReportDaoUtils;
  }

}
