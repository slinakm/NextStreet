package com.example.nextstreet.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ActivitySignupBinding;
import com.example.nextstreet.utilities.TextObserver;
import com.parse.ParseUser;

public class SignupActivity extends SignupAbstractActivity {

  private static final String TAG = SignupActivity.class.getSimpleName();
  private static final boolean isDriver = false;

  @Override
  protected String getTAG() {
    return TAG;
  }

  @Override
  protected boolean isDriver() {
    return isDriver;
  }
}
