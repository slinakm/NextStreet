package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
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
