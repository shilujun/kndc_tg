package com.cashhub.cash.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Config {
  @Id(autoincrement = true)
  private Long id;

  @Unique
  private String configKey;

  @Property
  private String configValue;

  @Generated(hash = 918391489)
  public Config(Long id, String configKey, String configValue) {
      this.id = id;
      this.configKey = configKey;
      this.configValue = configValue;
  }

  @Generated(hash = 589037648)
  public Config() {
  }

  public Long getId() {
      return this.id;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public String getConfigKey() {
      return this.configKey;
  }

  public void setConfigKey(String configKey) {
      this.configKey = configKey;
  }

  public String getConfigValue() {
      return this.configValue;
  }

  public void setConfigValue(String configValue) {
      this.configValue = configValue;
  }
}
