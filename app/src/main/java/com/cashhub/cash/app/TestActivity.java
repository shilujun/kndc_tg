package com.cashhub.cash.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

  private static final String TAG = "TestActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
    builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "onClick: continue");
      }
    });
    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "onClick: cancel");
        finish();
      }
    });
    final AlertDialog dialog = builder.create();
    dialog.show();
  }
}