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
import com.example.nextstreet.listeners.TextObserver;
import com.parse.ParseUser;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_PROFILEPIC = "profilePic";

    private SignupViewModel signupViewModel;
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupViewModel =
                ViewModelProviders.of(this).get(SignupViewModel.class);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                Toast.makeText(SignupActivity.this,
                        R.string.toast_required_fields_err, Toast.LENGTH_SHORT).show();
            } else if (!password2.equals(password)) {
                Toast.makeText(SignupActivity.this,
                        R.string.toast_match_password, Toast.LENGTH_SHORT).show();
            } else {
                signupUser(username, password, firstName, lastName, email);
            }
        }
    }

    private void signupUser(final String username, String password, String firstName,
                            String lastName, String email) {
        ParseUser user = new ParseUser();
        user.put(KEY_FIRSTNAME, firstName);
        user.put(KEY_LASTNAME, lastName);
        user.setUsername(username);
        user.setPassword(password);

        if (!email.isEmpty()) {
            user.setEmail(email);
        }

        user.signUpInBackground(new SignupCallback(TAG, binding.getRoot(),
                this, username, password));
    }
}
