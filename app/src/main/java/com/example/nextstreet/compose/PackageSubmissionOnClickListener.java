package com.example.nextstreet.compose;

import android.view.View;

public class PackageSubmissionOnClickListener implements View.OnClickListener {

  private final String TAG = PackageSubmissionOnClickListener.class.getSimpleName();
  private final PackageSubmissionResponder packageSubmissionResponder;

  public PackageSubmissionOnClickListener(String username,
                                          PackageSubmissionResponder packageSubmissionResponder) {
    this.packageSubmissionResponder = packageSubmissionResponder;
  }

  @Override
  public void onClick(View view) {
    packageSubmissionResponder.checkPostable();
  }
}
