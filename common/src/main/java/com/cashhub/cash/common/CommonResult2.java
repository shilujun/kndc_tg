package com.cashhub.cash.common;

import java.util.Map;

public class CommonResult2 {
  private int code = 0;
  private String msg = "";
  private Object data;

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

  public Object getData() {
    return this.data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
