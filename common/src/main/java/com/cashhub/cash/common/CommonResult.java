package com.cashhub.cash.common;

import java.util.Map;

public class CommonResult {
  private int code = 0;
  private String msg = "";
  private Map<String, String> data;

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Map<String, String> getData() {
    return this.data;
  }

  public void setData(Map<String, String> data) {
    this.data = data;
  }
}
