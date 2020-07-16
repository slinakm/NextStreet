package com.example.nextstreet.ui.home.compose;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.ui.ProfileFragment;

public class ComposeFragmentOnClickListener implements View.OnClickListener {

    private String TAG;
    private AppCompatActivity activity;

    public ComposeFragmentOnClickListener(String TAG, AppCompatActivity activity) {
        this.TAG = TAG;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: profilePic");
        FragmentManager fm = activity.getSupportFragmentManager();
        ComposeFragment fragment = ComposeFragment.newInstance();
        fragment.show(fm, ProfileFragment.class.getSimpleName());
    }
}
