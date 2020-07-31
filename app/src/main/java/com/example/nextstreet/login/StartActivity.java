package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nextstreet.DriverMainActivity;
import com.example.nextstreet.MainActivity;
import com.example.nextstreet.databinding.ActivityStartBinding;
import com.parse.ParseUser;

public class StartActivity extends AppCompatActivity {

  private static final String TAG = StartActivity.class.getSimpleName();
  private ActivityStartBinding binding;

  // TODO: go automatically to home if user already logged in
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityStartBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    isLogedIn();

    binding.driverButton.setOnClickListener(new StartActivity.DriverOnClickListener());
    binding.userButton.setOnClickListener(new StartActivity.UserOnClickListener());
  }

  private class DriverOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      Log.i(TAG, "signupOnClickListener onClick: chose driver");
      goActivity(DriverLoginActivity.class);
    }
  }

  private class UserOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      Log.i(TAG, "loginOnClickListener onClick: chose user");
      goActivity(LoginActivity.class);
    }
  }

  private boolean isLogedIn() {
    ParseUser currUser = ParseUser.getCurrentUser();

    if (currUser == null) {
      return false;
    }

    boolean currUserIsDriver = (boolean) currUser.get(LoginAbstractActivity.KEY_ISDRIVER);

    if (currUserIsDriver) {
      goActivity(DriverMainActivity.class);
      return true;
    } else {
      goActivity(MainActivity.class);
      return true;
    }
  }

  private void goActivity(Class c) {
    Intent i = new Intent(this, c);
    startActivity(i);
    finish();
  }
}
