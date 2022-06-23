package com.cashhub.cash.app.db;

public class ConfigDaoStore {
  private volatile static ConfigDaoStore instance = new ConfigDaoStore();

  public static ConfigDaoStore getInstance() {
    return instance;
  }

  public void deleteAll() {
    DaoUtilsStore.getInstance().getConfigDaoUtils().deleteAll();
  }

  public void queryAll() {
    DaoUtilsStore.getInstance().getConfigDaoUtils().queryAll();
  }

}
