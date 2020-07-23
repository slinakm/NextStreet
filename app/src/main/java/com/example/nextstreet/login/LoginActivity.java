package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.databinding.ActivityLoginBinding;
import com.example.nextstreet.utilities.TextObserver;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import java.util.Arrays;

public class LoginActivity extends LoginAbstractActivity {
  private static final String TAG = LoginActivity.class.getSimpleName();

  @Override
  protected boolean isLoggedIn() {
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    return ParseUser.getCurrentUser() != null || isLoggedIn;
  }

  @Override
  protected void loginUser(String username, String password) {
    Log.i(TAG, "loginUser: " + username);

    ParseUser.logInInBackground(
            username, password, new LoginCallback(TAG, getBinding().getRoot(), this));
  }

  @Override
  protected String getTAG() {
    return TAG;
  }

  @Override
  protected void signUpUser() {
    goActivity(SignupActivity.class);
  }


}
