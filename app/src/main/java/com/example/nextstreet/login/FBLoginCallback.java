package com.example.nextstreet.login;

import android.app.Activity;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public class FBLoginCallback implements FacebookCallback<LoginResult> {

    Activity activity;

    public FBLoginCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    @Override
    public void onCancel() {
        activity.setResult(Activity.RESULT_CANCELED);
        activity.finish();
    }

    @Override
    public void onError(FacebookException error) {

    }
}
