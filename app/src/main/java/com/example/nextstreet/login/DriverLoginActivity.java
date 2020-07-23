package com.example.nextstreet.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

public class DriverLoginActivity extends LoginAbstractActivity {

    private static final String TAG = DriverLoginActivity.class.getSimpleName();

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected String getWrongScreenErrorMessage() {
        return getResources().getString(R.string.toast_login_driverScreenWrong);
    }

    @Override
    protected void goToSignup() {
        goActivity(DriverSignupActivity.class);
    }

    @Override
    protected Boolean isDriver() {
        return Boolean.TRUE;
    }

    @Override
    protected boolean isLoggedIn() {
        return false;
    }

}
