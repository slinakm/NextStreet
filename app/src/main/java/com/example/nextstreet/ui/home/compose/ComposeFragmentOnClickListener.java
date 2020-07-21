package com.example.nextstreet.ui.home.compose;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.ui.home.HomeFragment;

public class ComposeFragmentOnClickListener implements View.OnClickListener {

  private final String TAG = ComposeFragmentOnClickListener.class.getSimpleName();
  private final AppCompatActivity activity;

  public ComposeFragmentOnClickListener(AppCompatActivity activity) {
    this.activity = activity;
  }

  @Override
  public void onClick(View view) {
    Log.d(TAG, "onClick to open compose fragment");

    FragmentManager fm = activity.getSupportFragmentManager();
    if (HomeFragment.hasCurrRequest()) {
      ComposeCurrentFragment fragment =
          ComposeCurrentFragment.newInstance(HomeFragment.getCurrRequest());
      fragment.show(fm, ComposeCurrentFragment.class.getSimpleName());
    } else {
      ComposeFragment fragment = ComposeFragment.newInstance();
      fragment.show(fm, ComposeFragment.class.getSimpleName());
    }
  }
}
