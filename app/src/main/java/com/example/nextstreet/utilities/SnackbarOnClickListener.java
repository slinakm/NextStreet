package com.example.nextstreet.utilities;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarOnClickListener implements View.OnClickListener {
  private final String stringToDisplay;

  public SnackbarOnClickListener(String stringToDisplay) {
    this.stringToDisplay = stringToDisplay;
  }

  @Override
  public void onClick(View view) {
    Snackbar.make(view, stringToDisplay, Snackbar.LENGTH_LONG).show();
  }
}
