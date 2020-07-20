package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.ui.MainActivity;
import com.example.nextstreet.utilities.TextObserver;
import com.example.nextstreet.databinding.ActivityLoginBinding;
import com.facebook.CallbackManager;
import com.parse.ParseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String EMAIL = "email";

    private ActivityLoginBinding binding;

    private LoginViewModel loginViewModel;
    private CallbackManager callbackManager;

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

        loginViewModel.getUsername().observe(this, new TextObserver(binding.etUsername));
        loginViewModel.getPassword().observe(this, new TextObserver(binding.etPassword));

        binding.btnLogin.setOnClickListener(new LoginOnClickListener());
        binding.btnSignup.setOnClickListener(new SignupOnClickListener());

        callbackManager = CallbackManager.Factory.create();
        binding.btnFb.setReadPermissions(Arrays.asList(EMAIL));
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

        ParseUser.logInInBackground(username, password, new LoginCallback(TAG, binding.getRoot(), this) );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goActivity(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);
        finish();
    }
}
