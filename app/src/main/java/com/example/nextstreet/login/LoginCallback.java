package com.example.nextstreet.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginCallback implements LogInCallback {

  private String TAG;
  private View view;
  private Activity activity;

  public LoginCallback(String TAG, View view, Activity activity) {
    this.TAG = TAG;
    this.view = view;
    this.activity = activity;
  }

  @Override
  public void done(ParseUser user, ParseException e) {
    if (e != null) {
      // TODO: State whether user has wrong username/password or not
      Log.e(TAG, "loginUser: issue with login", e);

      Snackbar.make(view, R.string.toast_login_err, Snackbar.LENGTH_SHORT).show();
      return;
    }
    goActivity(MainActivity.class);
    Snackbar.make(view, R.string.toast_login_succ, Snackbar.LENGTH_SHORT).show();
  }

  private void goActivity(Class c) {
    Intent i = new Intent(activity, c);
    activity.startActivity(i);
    activity.finish();
  }
}
