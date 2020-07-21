package com.example.nextstreet.utilities;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class TextObserver implements Observer<String> {
  private final TextView textViewToObserve;

  public TextObserver(TextView textViewToObserve) {
    this.textViewToObserve = textViewToObserve;
  }

  @Override
  public void onChanged(@Nullable String updatedString) {
    textViewToObserve.setText(updatedString);
  }
}
