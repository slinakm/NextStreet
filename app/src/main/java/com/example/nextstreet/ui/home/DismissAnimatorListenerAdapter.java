package com.example.nextstreet.ui.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class DismissAnimatorListenerAdapter extends AnimatorListenerAdapter {
  View view;

  public DismissAnimatorListenerAdapter(View view) {
    this.view = view;
  }

  @Override
  public void onAnimationEnd(Animator animation) {
    super.onAnimationEnd(animation);
    view.setVisibility(View.GONE);
  }
}
