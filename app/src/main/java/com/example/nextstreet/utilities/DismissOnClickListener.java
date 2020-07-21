package com.example.nextstreet.utilities;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class DismissOnClickListener implements View.OnClickListener {

  private static final String TAG = DismissOnClickListener.class.getSimpleName();
  private final DialogFragment fragment;

  public DismissOnClickListener(DialogFragment fragment) {
    this.fragment = fragment;
  }

  public void onClick(View view) {
    Log.i(TAG, "onClick: dialog fragment dismissed");
    fragment.dismiss();
  }
}
