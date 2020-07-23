package com.example.nextstreet.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupCallback implements SignUpCallback {

  private String TAG;
  private View view;
  private Activity activity;
  private String username;
  private String password;
  private boolean isDriver;

  public SignupCallback(
      String TAG, View view, Activity activity, String username, String password,
      boolean isDriver) {
    this.TAG = TAG;
    this.view = view;
    this.activity = activity;
    this.username = username;
    this.password = password;
    this.isDriver = isDriver;
  }

  @Override
  public void done(ParseException e) {
    if (e == null) {
      Log.i(TAG, "done: Signup " + username + " success!");
      Snackbar.make(view, R.string.toast_success, Snackbar.LENGTH_SHORT).show();
      goActivity(MainActivity.class);
    } else {
      Log.e(TAG, "done: Signup error", e);
      Snackbar.make(view, R.string.toast_signup_err, Snackbar.LENGTH_SHORT).show();
    }
  }

  private void goActivity(Class c) {
    ParseUser.logInInBackground(username, password, new LoginCallback(TAG, view, activity, isDriver));

    Intent i = new Intent(activity, c);
    activity.startActivity(i);
    activity.finish();
  }
}
