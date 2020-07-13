package com.example.nextstreet.listeners;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.ComposeFragment;
import com.example.nextstreet.ProfileFragment;

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
