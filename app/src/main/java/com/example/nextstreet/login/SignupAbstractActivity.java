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
import com.example.nextstreet.utilities.BackOnClickListener;
import com.example.nextstreet.utilities.BackResponder;
import com.example.nextstreet.utilities.TextObserver;
import com.parse.ParseUser;

public abstract class SignupAbstractActivity extends AppCompatActivity implements BackResponder {

  public static final String KEY_FIRSTNAME = "firstName";
  public static final String KEY_LASTNAME = "lastName";
  public static final String KEY_PROFILEPIC = "profilePic";
  public static final String KEY_ISDRIVER = "isDriver";

  private SignupViewModel signupViewModel;
  private ActivitySignupBinding binding;

  protected abstract String getTAG();

  protected abstract boolean isDriver();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    signupViewModel = ViewModelProviders.of(this).get(SignupViewModel.class);

    binding = ActivitySignupBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.signupButton.setOnClickListener(new SignupOnClickListener());
    binding.backImageView.setOnClickListener(new BackOnClickListener(this));

    signupViewModel.getUsername().observe(this, new TextObserver(binding.etUsername));
    signupViewModel.getPassword().observe(this, new TextObserver(binding.etPassword));
    signupViewModel.getPasswordAgain().observe(this, new TextObserver(binding.etPassword2));
    signupViewModel.getEmail().observe(this, new TextObserver(binding.etEmail));
  }

  @Override
  public void goBack() {
    Log.i(getTAG(), "backOnClickListener onClick: go back");

    finish();
  }

  private void signupUser(
      final String username,
      String password,
      String firstName,
      String lastName,
      String email,
      boolean isDriver) {
    ParseUser user = new ParseUser();
    user.put(KEY_FIRSTNAME, firstName);
    user.put(KEY_LASTNAME, lastName);
    user.put(KEY_ISDRIVER, isDriver);
    user.setUsername(username);
    user.setPassword(password);

    if (!email.isEmpty()) {
      user.setEmail(email);
    }

    user.signUpInBackground(
        new SignupCallback(getTAG(), binding.getRoot(), this, username, password, isDriver));
  }

  private class SignupOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      Log.i(getTAG(), "onClick: signup button clicked");

      String firstName = binding.etFirstName.getText().toString();
      String lastName = binding.etLastName.getText().toString();
      String username = binding.etUsername.getText().toString();
      String password = binding.etPassword.getText().toString();
      String password2 = binding.etPassword2.getText().toString();
      String email = binding.etEmail.getText().toString();

      if (firstName.isEmpty()
          || lastName.isEmpty()
          || username.isEmpty()
          || password.isEmpty()
          || password2.isEmpty()) {
        Toast.makeText(
                SignupAbstractActivity.this, R.string.toast_required_fields_err, Toast.LENGTH_SHORT)
            .show();
      } else if (!password2.equals(password)) {
        Toast.makeText(
                SignupAbstractActivity.this, R.string.toast_match_password, Toast.LENGTH_SHORT)
            .show();
      } else {
        signupUser(username, password, firstName, lastName, email, isDriver());
      }
    }
  }
}
