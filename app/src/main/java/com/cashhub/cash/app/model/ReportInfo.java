package com.cashhub.cash.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class ReportInfo {
  @Id(autoincrement = true)
  private Long id;

  @Property
  private String content;

  @Property
  private String type;

  @Property
  private Long date;

  @Generated(hash = 1162818968)
  public ReportInfo(Long id, String content, String type, Long date) {
      this.id = id;
      this.content = content;
      this.type = type;
      this.date = date;
  }

  @Generated(hash = 1519617947)
  public ReportInfo() {
  }

  public Long getId() {
      return this.id;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public String getContent() {
      return this.content;
  }

  public void setContent(String content) {
      this.content = content;
  }

  public String getType() {
      return this.type;
  }

  public void setType(String type) {
      this.type = type;
  }

  public Long getDate() {
      return this.date;
  }

  public void setDate(Long date) {
      this.date = date;
  }
}
