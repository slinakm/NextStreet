package com.example.nextstreet.login;

import com.example.nextstreet.R;

public class LoginActivity extends LoginAbstractActivity {
  private static final String TAG = LoginActivity.class.getSimpleName();

  @Override
  protected String getTAG() {
    return TAG;
  }

  @Override
  protected Boolean isDriver() {
    return Boolean.FALSE;
  }

  @Override
  protected String getWrongScreenErrorMessage() {
    return getResources().getString(R.string.toast_login_userScreenWrong);
  }

  @Override
  protected void goToSignup() {
    goActivity(SignupActivity.class);
  }
}
