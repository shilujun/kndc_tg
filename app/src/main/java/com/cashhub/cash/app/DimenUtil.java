package com.cashhub.cash.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @date: 2022/6/25
 */
public class DimenUtil {
  private String fileRoot = "../res";

  public static void gen() {
    //以此文件夹下的dimens.xml文件内容为初始值参照
    File file = new File("/src/main/res/values-sw375dp/dimens.xml");

    BufferedReader reader = null;
    StringBuilder sw360_default = new StringBuilder();
    StringBuilder sw320 = new StringBuilder();
    StringBuilder sw360 = new StringBuilder();
    StringBuilder sw393 = new StringBuilder();
    StringBuilder sw411 = new StringBuilder();
    StringBuilder sw420 = new StringBuilder();
    StringBuilder sw480 = new StringBuilder();
    StringBuilder sw560 = new StringBuilder();

    try {
      System.out.println("基准宽度dp值：[ 375 dp ]");
      System.out.println("本次待适配的宽度dp值: [ 320, 360, 393, 411, 420, 480, 560 ]");
      reader = new BufferedReader(new FileReader(file));
      String tempString;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        if (tempString.contains("</dimen>")) {
          //tempString = tempString.replaceAll(" ", "");
          String start = tempString.substring(0, tempString.indexOf(">") + 1);
          String end = tempString.substring(tempString.lastIndexOf("<") - 2);
          //截取<dimen></dimen>标签内的内容，从>右括号开始，到左括号减2，取得配置的数字
          Double num = Double.parseDouble
              (tempString.substring(tempString.indexOf(">") + 1,
                  tempString.indexOf("</dimen>") - 2));
          //根据不同的尺寸，计算新的值，拼接新的字符串，并且结尾处换行。
          sw360_default.append(start).append(formatDouble(num * 360 / 375)).append(end)
              .append("\r\n");
          sw320.append(start).append(formatDouble(num * getBaseDp(320))).append(end).append("\r\n");
          sw360.append(start).append(formatDouble(num * getBaseDp(360))).append(end).append("\r\n");
          sw393.append(start).append(formatDouble(num * getBaseDp(393))).append(end).append("\r\n");
          sw411.append(start).append(formatDouble(num * getBaseDp(411))).append(end).append("\r\n");
          sw420.append(start).append(formatDouble(num * getBaseDp(420))).append(end).append("\r\n");
          sw480.append(start).append(formatDouble(num * getBaseDp(480))).append(end).append("\r\n");
          sw560.append(start).append(formatDouble(num * getBaseDp(560))).append(end).append("\r\n");
        } else {
          sw360_default.append(tempString).append("\r\n");
          sw320.append(tempString).append("\r\n");
          sw360.append(tempString).append("\r\n");
          sw393.append(tempString).append("\r\n");
          sw411.append(tempString).append("\r\n");
          sw420.append(tempString).append("\r\n");
          sw480.append(tempString).append("\r\n");
          sw560.append(tempString).append("\r\n");
        }
      }

      reader.close();

      //将新的内容，写入到指定的文件中去
      String sw360_default_file = "./readx-ui/src/main/res/values/dimens.xml";
      writeFile(sw360_default_file, sw360_default.toString());

      String sw320_file = "./readx-ui/src/main/res/values-sw320dp/dimens.xml";
      writeFile(sw320_file, sw320.toString());

      String sw360_file = "./readx-ui/src/main/res/values-sw360dp/dimens.xml";
      writeFile(sw360_file, sw360.toString());

      String sw393_file = "./readx-ui/src/main/res/values-sw393dp/dimens.xml";
      writeFile(sw393_file, sw393.toString());

      String sw411_file = "./readx-ui/src/main/res/values-sw411dp/dimens.xml";
      writeFile(sw411_file, sw411.toString());

      String sw420_file = "./readx-ui/src/main/res/values-sw420dp/dimens.xml";
      writeFile(sw420_file, sw420.toString());

      String sw480_file = "./readx-ui/src/main/res/values-sw480dp/dimens.xml";
      writeFile(sw480_file, sw480.toString());

      String sw560_file = "./readx-ui/src/main/res/values-sw560dp/dimens.xml";
      writeFile(sw560_file, sw560.toString());
      System.out.println("执行成功");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }

  }


  public static float getBaseDp(int deviceWidthDp) {
    float sBaseDp;
    if (deviceWidthDp < 450) {
      sBaseDp = deviceWidthDp * 1.0f / 375;
    } else if (deviceWidthDp < 750) {
      sBaseDp = (float) (1.2 + 4 * (deviceWidthDp - 450) * 1.0f / 750 / 16);
    } else if (deviceWidthDp < 1000) {
      sBaseDp = (float) (1.3 + 8 * (deviceWidthDp - 750) * 1.0f / 1250 / 16);
    } else {
      sBaseDp = (float) (1.4 + 8 * (deviceWidthDp - 1000) * 1.0f / 1500 / 16);
    }
    return sBaseDp;
  }

  public static String formatDouble(Double in) {
    return new java.text.DecimalFormat("0.0000").format(in);
  }


  /**
   * 写入方法
   */

  public static void writeFile(String file, String text) {
    PrintWriter out = null;
    try {
      out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
      out.println(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
    out.close();
  }

  public static void main(String[] args) {
    gen();
  }
}
