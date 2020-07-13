package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ActivityLoginBinding;
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

        loginViewModel.getUsername().observe(this, new LoginActivity.textObserver(binding.etUsername));
        loginViewModel.getPassword().observe(this, new LoginActivity.textObserver(binding.etPassword));

        binding.btnSignup.setOnClickListener(new signupOnClickListener());
    }

    private class textObserver implements Observer<String> {
        TextView tv;

        private textObserver(TextView tv) {
            this.tv = tv;
        }

        @Override
        public void onChanged(@Nullable String s) {
            tv.setText(s);
        }
    }

    private class signupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "signupOnClickListener onClick: ");
            goActivity(SignupActivity.class);
        }
    }
    
    private void goActivity(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);
        finish();
    }
}
