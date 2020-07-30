package com.example.nextstreet.profile;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.nextstreet.login.StartActivity;
import com.parse.ParseUser;

/** Logout button */
class LogoutOnClickListener implements View.OnClickListener {
  private static final String TAG = LogoutOnClickListener.class.getSimpleName();
  private Activity context;

  LogoutOnClickListener(Activity context) {
    this.context = context;
  }

  public void onClick(View view) {
    Log.i(TAG, "onClick: submit button was clicked by user");

    ParseUser.logOut();

    Intent i = new Intent(context, StartActivity.class);
    context.startActivity(i);
    context.finish();
  }
}
