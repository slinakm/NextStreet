package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nextstreet.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

  private static final String TAG = StartActivity.class.getSimpleName();
  private ActivityStartBinding binding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityStartBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

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

  private void goActivity(Class c) {
    Intent i = new Intent(this, c);
    startActivity(i);
  }
}
