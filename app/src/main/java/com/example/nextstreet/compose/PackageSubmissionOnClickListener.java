package com.example.nextstreet.compose;

import android.view.View;

public class PackageSubmissionOnClickListener implements View.OnClickListener {

  private final String TAG = PackageSubmissionOnClickListener.class.getSimpleName();
  private final ComposeFragment fragment;

  public PackageSubmissionOnClickListener(String username, ComposeFragment fragment) {
    this.fragment = fragment;
  }

  @Override
  public void onClick(View view) {
    fragment.checkPostable();
  }
}
