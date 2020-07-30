package com.example.nextstreet.login;

import com.example.nextstreet.R;

public class DriverLoginActivity extends LoginAbstractActivity {

  private static final String TAG = DriverLoginActivity.class.getSimpleName();

  @Override
  protected String getTAG() {
    return TAG;
  }

  @Override
  protected String getWrongScreenErrorMessage() {
    return getResources().getString(R.string.toast_login_driverScreenWrong);
  }

  @Override
  protected void goToSignup() {
    goActivity(DriverSignupActivity.class);
  }

  @Override
  protected Boolean isDriver() {
    return Boolean.TRUE;
  }
}
