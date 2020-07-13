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
import com.example.nextstreet.listeners.LoginCallback;
import com.example.nextstreet.listeners.TextObserver;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();

    private SignupViewModel signupViewModel;
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupViewModel =
                ViewModelProviders.of(this).get(SignupViewModel.class);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar ab = getSupportActionBar();
        assert ab != null;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        binding.btnSignup.setOnClickListener(new SignupOnClickListener());

        signupViewModel.getUsername().observe(this, new TextObserver(binding.etUsername));
        signupViewModel.getPassword().observe(this, new TextObserver(binding.etPassword));
        signupViewModel.getPasswordAgain().observe(this, new TextObserver(binding.etPassword2));
        signupViewModel.getEmail().observe(this, new TextObserver(binding.etEmail));
    }

    private class SignupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: signup button clicked");

            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();
            String password2 = binding.etPassword2.getText().toString();
            String email = binding.etEmail.getText().toString();

            if (username.isEmpty()
                    || password.isEmpty()
                    || password2.isEmpty()) {
                Toast.makeText(SignupActivity.this,
                        R.string.toast_required_fields_err, Toast.LENGTH_SHORT).show();
            } else if (!password2.equals(password)) {
                Toast.makeText(SignupActivity.this,
                        R.string.toast_match_password, Toast.LENGTH_SHORT).show();
            } else {
                signupUser(username, password, email);
            }
        }
    }

    private void signupUser(final String username, String password, String email) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        if (!email.isEmpty()) {
            user.setEmail(email);
        }

        user.signUpInBackground();

        ParseUser.logInInBackground(new LoginCallback(TAG, binding.getRoot(), this));
    }

    private void goActivity(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);
        finish();
    }
}
