package com.example.nextstreet.listeners;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.ProfileFragment;
import com.example.nextstreet.login.LoginActivity;
import com.parse.ParseUser;

public class DismissOnClickListener implements View.OnClickListener{

    private String TAG;
    private DialogFragment fragment;

    public DismissOnClickListener(String TAG, DialogFragment fragment) {
        this.TAG = TAG;
        this.fragment = fragment;
    }

    public void onClick(View view) {
        Log.i(TAG, "onClick: dialog fragment dismissed");
        fragment.dismiss();
    }
}
