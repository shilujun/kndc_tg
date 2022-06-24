package com.cashhub.cash.common.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

  public static String FixString(String src, int len, boolean isAddDel) {
    if (src.length() <= len) {
      return src;
    }
    return src.substring(0, len) + (isAddDel ? "..." : "");
  }

  /**
   * 判断字符串是否为数字字符串
   *
   * @return 是，返回true，否则返回false
   */
  public static boolean isNumeric(String str) {
    if (TextUtils.isEmpty(str)) {
      return false;
    }
    for (int i = str.length(); --i >= 0; ) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 判断字符串是否为空
   *
   * @return 如果str为NULL或者空字符串或者“null”，返回true，否则返回false
   */
  public static boolean isBlank(String str) {
    if (str == null || str.trim().length() == 0 || str.equalsIgnoreCase("null")) {
      return true;
    }
    return false;
  }

  public static boolean isBlank(CharSequence str) {
    if (str == null || str.toString().trim().length() == 0 || str.toString()
      .equalsIgnoreCase("null")) {
      return true;
    }
    return false;
  }

  public static String GetUrlDomain(String url) {
    int startpos = url.indexOf("://") + 3;
    int endpos = url.substring(startpos).indexOf("/");
    return url.substring(startpos).substring(0, endpos);
  }

  public static String FixTime4(long time) {
    long n = time - System.currentTimeMillis();
    if (n <= (1000l * 60 * 60 * 24 * 7)) {
      long day = n / (1000l * 60 * 60 * 24);
      return day + "";
    }
    return null;
  }

  public static String FixTime3(long time) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
    Date dt = new Date(time);
    String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
    return sDateTime;
  }

  public static String FixTime5(long time) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    Date dt = new Date(time);
    String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006 21:08:00
    return sDateTime;
  }

  public static String getTime2(long l) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date d = new Date(l);
    return sDateFormat.format(d);

  }

  public static String fixStr(String str) {
    String[] removeStr = {"\r", "<br>", "　", "</br>", "\r<br>"};
    return trans(str, removeStr);

  }

  public static boolean isToday(long when, long today) {
    Time time = new Time();
    time.set(when);

    int thenYear = time.year;
    int thenMonth = time.month;
    int thenMonthDay = time.monthDay;

    time.set(today);
    return (thenYear == time.year) && (thenMonth == time.month) && (thenMonthDay == time.monthDay);
  }

  public static boolean isThisYear(long when, long today) {
    Time time = new Time();
    time.set(when);

    int thenYear = time.year;

    time.set(today);
    return thenYear == time.year;
  }

  private static String trans(String str, String[] removeStr) {
    for (String rs : removeStr) {
      str = str.replaceAll(rs, "");
    }
    // System.out.println("str==========="+str);
    return str;

  }

  public static String fixNewStr(String str) {
    String[] reStr = {"&nbsp;", " "};
    for (String rs : reStr) {
      str = str.replaceAll(rs, "");
    }
    return str;
  }

  public static String nbspToSpace(String str) {
    String[] reStr = {"&nbsp;"};
    for (String rs : reStr) {
      str = str.replaceAll(rs, " ");
    }
    return str;
  }

  public static String splitStr(String str) {
    if (str.length() > 80) {
      return str.substring(0, 80);
    } else {
      return str;
    }
  }

  public static String getTime(long l) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd");
    Date d = new Date(l);
    return sDateFormat.format(d);

  }

  public static String getTime02(long l) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("MM.dd");
    Date d = new Date(l);
    return sDateFormat.format(d);

  }

  public static String[] getTime03(long l) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date d = new Date(l);
    String formatStr = sDateFormat.format(d);
    String[] array = formatStr.split("-");
    return array;

  }

  public static String fixTime(long l) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date d = new Date(l);
    return sDateFormat.format(d);

  }

  public static String fixTime01(long l) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date d = new Date(l);
    return sDateFormat.format(d);

  }

  public static String formatDate(Date mDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(mDate);
  }

  public static String formatData02(Date mData) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return df.format(mData);
  }

  public static String formatData03(Date mData) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    return df.format(mData);
  }

  public static String getDate(String timeStr) {
    Date date = new Date(Long.parseLong(timeStr.trim()));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    String dateString = formatter.format(date);
    return getIntTime(dateString);
  }

  /**
   * 得到yyyy-MM-dd格式时间
   */
  public static String getDate02(String timeStr) {
    if (timeStr.equals("")) {
      return "";
    }
    Date date = new Date(Long.parseLong(timeStr.trim()));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(date);
    return dateString;
  }

  private static String getIntTime(String time) {
    String timeStr[] = time.split("-");
    String timeInt = timeStr[0] + timeStr[1] + timeStr[2];
    return timeInt;
  }

  /**
   * 得到系统当前时间
   */
  public static String getNowTime() {
    Date dataTime = new Date();
    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
    return simpleFormat.format(dataTime);
  }

  /**
   * 得到yyyy-MM-dd格式时间
   */
  public static String getDateTwo(String timeStr) {
    Date date = new Date(Long.parseLong(timeStr.trim()));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(date);
    return dateString;
  }

  /**
   * 查找字符串中包含了另�?个字符串几次
   */
  public static int findStrContainsCount(String s, String toFind) {
    if (s == null || s.length() == 0) {
      return 0;
    }
    int index = 0;
    int count = 0;
    while (index != -1) {
      if (s == null || s.length() == 0) {
        break;
      }
      index = s.indexOf(toFind);
      if (index == -1) {
        break;
      }
      int length = toFind.length();
      s = s.substring(index + length);
      count++;
    }
    return count;
  }

  public static String getStr(Context ctx, int id) {
    return ctx.getString(id);
  }

  public static String filterString(String s) {
    return s.replaceAll("<br>", "\r\n").replaceAll("&nbsp;", "").replaceAll("�?", "")
      .replaceAll("<b>", "").replaceAll("</b>", "");
  }

  public static String getPercent(int x, int total) {

    String result = "";// 接受百分比的�?
    double x_double = x * 1.0;
    double tempresult = x_double / total;
    // NumberFormat nf = NumberFormat.getPercentInstance();
    // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几�?
    DecimalFormat df1 = new DecimalFormat("0.00%"); // ##.00%
    // 百分比格式，后面不足2位的�?0补齐
    // result=nf.format(tempresult);
    result = df1.format(tempresult);
    return result;
  }

  public static String getStringByLength(String s, int length) {
    if (TextUtils.isEmpty(s)) {
      return "";
    }
    if (s.length() <= length) {
      return s;
    }
    return s.substring(0, length);
  }

  public static String[] filterUrl(Object object) {
    String[] strs = ((String) object).split(",");
    // ["http:\/\/fast-cdn.dianjoy.com\/dev\/upload\/ad_url\/201405\/0_f5d776257701262c807d2e6ca3e4e7bc_h_400.jpg"
    // "http:\/\/fast-cdn.dianjoy.com\/dev\/upload\/ad_url\/201406\/0_e6bc59ce5d385feed850b38621b82f9f_h_400.jpeg"]
    String[] desStrs = new String[strs.length];
    for (int i = 0; i < strs.length; i++) {
      desStrs[i] = strs[i].replaceAll("[\\\\\\[\\]\"]", "");
    }
    return desStrs;
  }

  public static boolean equalsIgnoreCase(String str1, String str2) {
    if (str1 == null && str2 != null) {
      return false;
    } else if (str1 != null && str2 == null) {
      return false;
    } else if (str1 == null && str2 == null) {
      return true;
    } else {
      return str1.equalsIgnoreCase(str2);
    }

  }

  public static String fixNullStringToEmpty(String str) {
    if (str == null) {
      return "";
    }
    return str;
  }

  /**
   * <p> Title: decimalFormat </p> <p> Description: 2个double相处,保留2位小数点 </p>
   */
  public static String decimalFormat(double a, double b) {
    DecimalFormat df = new DecimalFormat("0.00");
    if (b == 0) {
      return "";
    }
    final double c = a / b;
    return df.format(c);

  }

  /**
   * 去除字符串中的所有空格
   */
  public static String removeBlankSpace(String string) {
    return string.replaceAll("\\s*", "");
  }

  public static boolean EmailFormat(String email) {// 邮箱判断正则表达式
    Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    Matcher mc = pattern.matcher(email);
    return mc.matches();
  }

  public static String FixWords99Public(long words) {
    if (words > 99) {
      return 99 + "+";
    }
    return words + "";
  }

  public static String FixWordsFanSiBang(long words) {
    if (words >= 500) {
      return "500+";
    }
    return words + "";
  }

  /**
   * 替换Html标记
   */
  public static String replaceTag(String input) {
    if (!hasSpecialChars(input)) {
      return input;
    }
    StringBuffer filtered = new StringBuffer(input.length());
    char c;
    for (int i = 0; i <= input.length() - 1; i++) {
      c = input.charAt(i);
      switch (c) {
        case '<':
          filtered.append("&lt;");
          break;
        case '>':
          filtered.append("&gt;");
          break;
        case '"':
          filtered.append("&quot;");
          break;
        case '&':
          filtered.append("&amp;");
          break;
        default:
          filtered.append(c);
      }

    }
    return (filtered.toString());
  }

  /**
   * 基本功能：判断标记是否存在
   *
   * @return boolean
   */
  public static boolean hasSpecialChars(String input) {
    boolean flag = false;
    if ((input != null) && (input.length() > 0)) {
      char c;
      for (int i = 0; i <= input.length() - 1; i++) {
        c = input.charAt(i);
        switch (c) {
          case '>':
            flag = true;
            break;
          case '<':
            flag = true;
            break;
          case '"':
            flag = true;
            break;
          case '&':
            flag = true;
            break;
        }
      }
    }
    return flag;
  }

  /**
   * 为TextView加入彩色问题，注意是append方式
   *
   * @param keyword 字符串
   * @param color 颜色
   * @param textView 要加的textview
   */
  public static void setColorSpanTextAppend(String keyword, int color, TextView textView) {
    if (TextUtils.isEmpty(keyword) || textView == null) {
      return;
    }
    SpannableString spanString = new SpannableString(keyword);
    ForegroundColorSpan span = new ForegroundColorSpan(color);
    spanString.setSpan(span, 0, keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    textView.append(spanString);
  }

  public static void setForegroundColorSpan(String destinationString, String key,
    TextView textView) {
    if (!destinationString.contains(key)) {
      return;
    }
    if (destinationString.length() == 0 || key.length() == 0) {
      return;
    }
    if (destinationString.length() < key.length()) {
      return;
    }

    SpannableString ss = new SpannableString(destinationString);
    int firstIndex = destinationString.indexOf(key, 0);
    if (firstIndex > -1) {
      ss.setSpan(new ForegroundColorSpan(0xff65C541), firstIndex, firstIndex + key.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    textView.setText(ss);
  }

  /**
   * 去除String中的HTML标签
   */
  public static String removeHtml(String string) {
    String regExHtml = "<[^>]+>"; //定义HTML标签的正则表达式
    Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
    Matcher mHtml = pHtml.matcher(string);
    string = mHtml.replaceAll(""); //过滤html标签

    return string.trim();
  }

  /**
   * 向右补位
   */
  public static String padRight(String t, int n, char c) {
    StringBuilder sb = new StringBuilder();
    sb.append(t);
    int g = n - t.length();
    for (int i = 0; i < g; i++) {
      sb.append(c);
    }
    return sb.toString();
  }

  /**
   * 将数字转为 333,333,333格式
   */
  public static String formatNumToMoney(String text) {
    String str1 = new StringBuilder(text).reverse().toString();     //先将字符串颠倒顺序
    String str2 = "";
    for (int i = 0; i < str1.length(); i++) {
      if (i * 3 + 3 > str1.length()) {
        str2 += str1.substring(i * 3, str1.length());
        break;
      }
      str2 += str1.substring(i * 3, i * 3 + 3) + ",";
    }
    if (str2.endsWith(",")) {
      str2 = str2.substring(0, str2.length() - 1);
    }
    return new StringBuilder(str2).reverse().toString();
  }


  /*^\s
  * 统一添加首行缩进 "(　)+(.*?)(　)+"  .replaceAll("　","")
  * */
  public static String textIndent(String item) {
    int index = item.indexOf("　");
    if (index == -1) {
      return item;
    }
    index = index + 2;
    String str1 = item.substring(0, index);
    String str2 = item.substring(index);
    str2 = str2.replaceAll("\r\n(\\s*)", "\r\n");
    item = str1 + str2;
    Pattern pattern = Pattern.compile("[\r\n]+");
    Matcher matcher = pattern.matcher(item);
    String result = matcher.replaceAll("\r\n　　");
    return result;
  }

  public static String replaceAllBlankAndLine(String str) {
    String[] removeStr = {"\n", "\r", " ", "<br>", "</br>"};
    return trans(str, removeStr).trim();
  }
}
