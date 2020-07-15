package com.example.nextstreet.ui;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.ui.ProfileFragment;

public class ProfileFragmentOnClickListener implements View.OnClickListener {

    private String TAG;
    private AppCompatActivity activity;

    public ProfileFragmentOnClickListener(String TAG, AppCompatActivity activity) {
        this.TAG = TAG;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: profilePic");
        FragmentManager fm = activity.getSupportFragmentManager();
        ProfileFragment fragment = ProfileFragment.newInstance();
        fragment.show(fm, ProfileFragment.class.getSimpleName());
    }
}
