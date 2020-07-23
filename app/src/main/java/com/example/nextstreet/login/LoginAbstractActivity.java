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
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public abstract class LoginAbstractActivity extends AppCompatActivity {

  protected static final String KEY_PASSWORD = "password";
  protected static final String KEY_USERNAME = "username";
  protected static final String KEY_ISDRIVER = "isDriver";

  private static final String FB_EMAIL = "email";
  private final CallbackManager callbackManager = CallbackManager.Factory.create();

  protected abstract boolean isLoggedIn();

  protected abstract Boolean isDriver();

  protected abstract String getTAG();

  protected abstract String getWrongScreenErrorMessage();

  protected abstract void goToSignup();

  private ActivityLoginBinding binding;

  private LoginViewModel loginViewModel;

  ActivityLoginBinding getBinding() {
    return binding;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityLoginBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    if (isLoggedIn()) {
      goActivity(MainActivity.class);
    }

    loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

    loginViewModel.getUsername().observe(this, new TextObserver(binding.etUsername));
    loginViewModel.getPassword().observe(this, new TextObserver(binding.etPassword));

    binding.loginButton.setOnClickListener(new LoginOnClickListener());
    binding.signupButton.setOnClickListener(new SignupOnClickListener());
    binding.backImageView.setOnClickListener(new BackOnClickListener());

    binding.fbSignupButton.setReadPermissions(Arrays.asList(FB_EMAIL));

    LoginManager.getInstance().registerCallback(callbackManager, new FBLoginCallback(this));
  }

  private class SignupOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      Log.i(getTAG(), "signupOnClickListener onClick: ");

      goToSignup();
    }
  }

  private class LoginOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      Log.i(getTAG(), "loginOnClickListener onClick: ");

      loginUser(binding.etUsername.getText().toString(), binding.etPassword.getText().toString());
    }
  }

  private class BackOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      Log.i(getTAG(), "backOnClickListener onClick: ");

      finish();
    }
  }

  private void loginUser(String username, String password) {
    Log.i(getTAG(), "loginUser: " + username);

    try {
      List<ParseUser> foundUsers =
          ParseUser.getQuery()
              .whereEqualTo(KEY_USERNAME, username)
              .include(KEY_ISDRIVER)
              .find();

      if (checkExistingUsers(foundUsers)) {
        Log.i(getTAG(), "loginUser: check was ok, now logging in " + username);
        ParseUser.logInInBackground(
                username, password, new LoginCallback(getTAG(), getBinding().getRoot(), this));
      }
    } catch (ParseException e) {
      Log.e(getTAG(), "loginUser: error finding " + username + " in query", e);
      Snackbar.make(binding.getRoot(), R.string.toast_login_err, Snackbar.LENGTH_SHORT).show();
    }
  }

  private boolean checkExistingUsers(List<ParseUser> foundUsers) {
    if (foundUsers.size() == 0) {
      Log.e(getTAG(), "checkExistingUsers: no users found");
      Snackbar.make(binding.getRoot(), R.string.toast_login_noUsers, Snackbar.LENGTH_SHORT)
          .show();
      return false;
    } else {
      boolean userCorrectScreen = false;
      for (ParseUser user : foundUsers) {
        if (user.get(KEY_ISDRIVER) == isDriver()) {
          userCorrectScreen = true;
        }
      }

      if (!userCorrectScreen) {
        Log.e(getTAG(), "checkExistingUsers: user signed into wrong screen");
        Snackbar.make(binding.getRoot(), getWrongScreenErrorMessage(), Snackbar.LENGTH_SHORT)
            .show();
        return false;
      }
    }
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }

  protected void goActivity(Class classToGoTo) {
    Intent i = new Intent(this, classToGoTo);
    startActivity(i);
    finish();
  }
}
