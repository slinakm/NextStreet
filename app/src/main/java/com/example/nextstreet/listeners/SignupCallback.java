package com.example.nextstreet.listeners;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
import com.example.nextstreet.login.SignupActivity;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class SignupCallback implements SignUpCallback {

    private String TAG;
    private View view;
    private Activity activity;
    private String username;

    public SignupCallback(String TAG, View view, Activity activity, String username) {
        this.TAG = TAG;
        this.view = view;
        this.activity = activity;
        this.username = username;
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            Log.i(TAG, "done: Signup " + username + " success!");
            Snackbar.make(view,
                    R.string.toast_success, Snackbar.LENGTH_SHORT).show();
            goActivity(MainActivity.class);
        } else {
            Log.e(TAG, "done: Signup error", e);
            Snackbar.make(view,
                    R.string.toast_signup_err, Snackbar.LENGTH_SHORT).show();
        }
    }


    private void goActivity(Class c) {
        Intent i = new Intent(activity, c);
        activity.startActivity(i);
        activity.finish();
    }
}
