package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            goActivity(MainActivity.class);
        }

        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);

        loginViewModel.getUsername().observe(this, new LoginActivity.TextObserver(binding.etUsername));
        loginViewModel.getPassword().observe(this, new LoginActivity.TextObserver(binding.etPassword));

        binding.btnLogin.setOnClickListener(new LoginOnClickListener());
        binding.btnSignup.setOnClickListener(new SignupOnClickListener());
    }

    private class TextObserver implements Observer<String> {
        TextView tv;

        private TextObserver(TextView tv) {
            this.tv = tv;
        }

        @Override
        public void onChanged(@Nullable String s) {
            tv.setText(s);
        }
    }

    private class SignupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "signupOnClickListener onClick: ");
            goActivity(SignupActivity.class);
        }
    }

    private class LoginOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "loginOnClickListener onClick: ");

            loginUser(binding.etUsername.getText().toString(),
                    binding.etPassword.getText().toString());
        }
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "loginUser: " + username);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // TODO: State whether user has wrong username/password or not
                    Log.e(TAG, "loginUser: issue with login", e);

                    Snackbar.make(binding.getRoot(), R.string.toast_login_err,
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                goActivity(MainActivity.class);
                Snackbar.make(binding.getRoot(), R.string.toast_login_succ,
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void goActivity(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);
        finish();
    }
}
