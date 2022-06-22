package com.cashhub.cash.app;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.cashhub.cash.app.databinding.ActivityWebviewBinding;

public class WebviewActivity extends AppCompatActivity {

  private AppBarConfiguration appBarConfiguration;
  private ActivityWebviewBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_webview);
    //获得控件
    WebView webView = (WebView) findViewById(R.id.wv_webview);
    //访问网页
    webView.loadUrl("https://www.baidu.com/");
    //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //使用WebView加载显示url
        view.loadUrl(url);
        //返回true
        return true;
      }
    });
  }
}