package com.example.nextstreet.login;

public class SignupActivity extends SignupAbstractActivity {

  private static final String TAG = SignupActivity.class.getSimpleName();
  private static final boolean isDriver = false;

  @Override
  protected String getTAG() {
    return TAG;
  }

  @Override
  protected boolean isDriver() {
    return isDriver;
  }
}
