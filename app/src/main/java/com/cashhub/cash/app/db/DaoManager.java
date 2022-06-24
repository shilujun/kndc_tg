package com.cashhub.cash.app.db;

import android.app.Application;
import com.cashhub.cash.app.greendao.DaoMaster;
import com.cashhub.cash.app.greendao.DaoSession;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 创建数据库、创建数据库表、包含增删改查的操作
 */
public class DaoManager {
  private static final String TAG = DaoManager.class.getSimpleName();
  private static final String DB_NAME = "user_info.db";

  private Application mApplication;

  //多线程中要被共享的使用volatile关键字修饰
  private volatile static DaoManager manager = new DaoManager();
  private DaoMaster mDaoMaster;
  private DaoMaster.DevOpenHelper mHelper;
  private DaoSession mDaoSession;

  /**
   * 单例模式获得操作数据库对象
   */
  public static DaoManager getInstance() {
    return manager;
  }

  public void init(Application application) {
    this.mApplication = application;
  }

  /**
   * 判断是否有存在数据库，如果没有则创建
   */
  public DaoMaster getDaoMaster() {
    if (mDaoMaster == null) {
      DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mApplication, DB_NAME, null);
      mDaoMaster = new DaoMaster(helper.getWritableDatabase());
    }
    return mDaoMaster;
  }

  /**
   * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
   */
  public DaoSession getDaoSession() {
    if (mDaoSession == null) {
      if (mDaoMaster == null) {
        mDaoMaster = getDaoMaster();
      }
      mDaoSession = mDaoMaster.newSession();
    }
    return mDaoSession;
  }

  /**
   * 关闭所有的操作，数据库开启后，使用完毕要关闭
   */
  public void closeConnection() {
    closeHelper();
    closeDaoSession();
  }

  public void closeHelper() {
    if (mHelper != null) {
      mHelper.close();
      mHelper = null;
    }
  }

  public void closeDaoSession() {
    if (mDaoSession != null) {
      mDaoSession.clear();
      mDaoSession = null;
    }
  }
}