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
  private final Fragment targetfragment;

  public ComposeFragmentOnClickListener(MapsPlaceSelectionResponder targetfragment) {
    this.targetfragment = (Fragment) targetfragment;
  }

  @Override
  public void onClick(View view) {
    Log.d(TAG, "onClick to open compose fragment");

    if (HomeFragment.hasCurrRequest()) {
      FragmentManager fm = targetfragment.getChildFragmentManager();
      ComposeCurrentFragment composeCurrentFragment =
          ComposeCurrentFragment.newInstance(HomeFragment.getCurrRequest());
      composeCurrentFragment.show(fm, ComposeCurrentFragment.class.getSimpleName());
    } else {
      FragmentManager fm = targetfragment.getParentFragmentManager();
      ComposeFragment composeFragment = ComposeFragment.newInstance();
      composeFragment.setTargetFragment(targetfragment, 0);
      composeFragment.show(fm, ComposeFragment.class.getSimpleName());
    }
  }
}
