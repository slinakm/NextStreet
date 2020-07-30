package com.example.nextstreet.login;

public class DriverSignupActivity extends SignupAbstractActivity {
  private static final String TAG = DriverSignupActivity.class.getSimpleName();
  private static final boolean isDriver = true;

  @Override
  protected String getTAG() {
    return TAG;
  }

  @Override
  protected boolean isDriver() {
    return isDriver;
  }
}
