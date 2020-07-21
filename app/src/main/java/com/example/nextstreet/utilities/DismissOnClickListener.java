package com.example.nextstreet.utilities;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class DismissOnClickListener implements View.OnClickListener {

  private String TAG;
  private DialogFragment fragment;

  public DismissOnClickListener(String TAG, DialogFragment fragment) {
    this.TAG = TAG;
    this.fragment = fragment;
  }

  public void onClick(View view) {
    Log.i(TAG, "onClick: dialog fragment dismissed");
    fragment.dismiss();
  }
}
