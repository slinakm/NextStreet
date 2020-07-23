package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.databinding.ActivityLoginBinding;
import com.example.nextstreet.utilities.TextObserver;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import java.util.Arrays;

public abstract class LoginAbstractActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private final CallbackManager callbackManager = CallbackManager.Factory.create();

    protected abstract boolean isLoggedIn();
    protected abstract void loginUser(String username, String password);
    protected abstract String getTAG();
    protected abstract void signUpUser();

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

        binding.fbSignupButton.setReadPermissions(Arrays.asList(EMAIL));

        LoginManager.getInstance().registerCallback(callbackManager, new FBLoginCallback(this));
    }



    private class SignupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i(getTAG(), "signupOnClickListener onClick: ");
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
