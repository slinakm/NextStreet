package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.nextstreet.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

public class DriverLoginActivity extends LoginAbstractActivity {

    private static final String TAG = DriverLoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void signUpUser() {
        goActivity(DriverSignupActivity.class);
    }

    @Override
    protected boolean isLoggedIn() {
        return false;
    }

    @Override
    protected void loginUser(String username, String password) {
        Log.i(TAG, "loginUser: " + username);

        ParseUser.logInInBackground(
                username, password, new LoginCallback(TAG, getBinding().getRoot(), this));
    }
}
