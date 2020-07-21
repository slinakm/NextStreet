package com.example.nextstreet.ui;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class ProfileFragmentOnClickListener implements View.OnClickListener {

  private static final String TAG = ProfileFragmentOnClickListener.class.getSimpleName();
  private final AppCompatActivity activity;

  public ProfileFragmentOnClickListener(AppCompatActivity activity) {
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
