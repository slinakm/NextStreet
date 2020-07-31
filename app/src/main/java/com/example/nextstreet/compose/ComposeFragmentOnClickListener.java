package com.example.nextstreet.compose;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.nextstreet.home.HomeFragment;
import com.example.nextstreet.home.MapsPlaceSelectionResponder;

public class ComposeFragmentOnClickListener implements View.OnClickListener {

  private final String TAG = ComposeFragmentOnClickListener.class.getSimpleName();
  private final Fragment fragment;

  public ComposeFragmentOnClickListener(MapsPlaceSelectionResponder fragment) {
    this.fragment = (Fragment) fragment;
  }

  @Override
  public void onClick(View view) {
    Log.d(TAG, "onClick to open compose fragment");

    FragmentManager fm = fragment.getChildFragmentManager();
    if (HomeFragment.hasCurrRequest()) {
      ComposeCurrentFragment fragment =
          ComposeCurrentFragment.newInstance(HomeFragment.getCurrRequest());
      fragment.show(fm, ComposeCurrentFragment.class.getSimpleName());
    } else {
      ComposeFragment fragment = ComposeFragment.newInstance();
      fragment.setTargetFragment(fragment, 0);
      fragment.show(fm, ComposeFragment.class.getSimpleName());
    }
  }
}
