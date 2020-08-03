package com.example.nextstreet.utilities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.nextstreet.R;

public class CircularRevealDialogFragment extends DialogFragment {

  private static final String TAG = CircularRevealDialogFragment.class.getSimpleName();

  /**
   * To be called in OnCreateView to animate the entire fragment.
   *
   * @param viewToAnimate, the view to animate (should be the root view).
   * @param animateEntireFragment, this determines whether a background color is also applied so
   *     that the entire fragment (and not just fragment contents) is animated.
   */
  protected void setUpOnLayoutListener(
      final View viewToAnimate, final boolean animateEntireFragment) {
    viewToAnimate.addOnLayoutChangeListener(
        new View.OnLayoutChangeListener() {
          @TargetApi(Build.VERSION_CODES.LOLLIPOP)
          @Override
          public void onLayoutChange(
              View v,
              int left,
              int top,
              int right,
              int bottom,
              int oldLeft,
              int oldTop,
              int oldRight,
              int oldBottom) {
            v.removeOnLayoutChangeListener(this);
            if (animateEntireFragment) {
              setUpForShowAnimation(viewToAnimate);
            } else {
              animateShowingFragment(viewToAnimate);
            }
          }
        });
  }

  /**
   * Sets up the dialog to animate the entire fragment.
   *
   * @param root, root view to animate
   */
  private void setUpForShowAnimation(final View root) {
    Log.i(TAG, "setUpForShowAnimation: ");
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(root).setCancelable(false);

    Dialog dialog = getDialog();

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    dialog.show();
    animateShowingFragment(root);
  }

  /**
   * A method where a view is revealed circularly from the bottom left corner.
   *
   * @param viewToAnimate, the view to animate and reveal
   */
  protected void animateShowingFragment(View viewToAnimate) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Log.i(TAG, "animateShowingFragment: animating");

      // get the center for the clipping circle: if you want to change where the
      // animation starts, change these two values
      int cx = 0;
      int cy = 0;

      // get the final radius for the clipping circle: if you change cx and cy,
      // change these just in case too
      float finalRadius = (float) Math.hypot(viewToAnimate.getWidth(), viewToAnimate.getHeight());

      // create the animator for this view (the start radius is zero)
      Animator anim =
          ViewAnimationUtils.createCircularReveal(viewToAnimate, cx, cy, 0f, finalRadius);
      anim.setDuration(getResources().getInteger(R.integer.composeFragment_time_appearing));
      anim.setInterpolator(new FastOutSlowInInterpolator());

      startColorAnimation(
          viewToAnimate,
          getResources().getColor(R.color.animation_start_color),
          getResources().getColor(R.color.animation_end_color),
          getResources().getInteger(R.integer.composeFragment_time_appearing));
      // make the view visible and start the animation
      viewToAnimate.setVisibility(View.VISIBLE);
      anim.start();

    } else {
      // set the view to invisible without a circular reveal animation below Lollipop
      viewToAnimate.setVisibility(View.VISIBLE);
    }
  }

  /**
   * A method to animate a growing circle during reveal transitions.
   *
   * @param viewToAnimate, the view to animate is set final since it is accessed in inner class
   * @param startColor, start color for animation
   * @param endColor, ending color for animation
   * @param duration, length of animation
   */
  private void startColorAnimation(
      final View viewToAnimate, @ColorInt int startColor, @ColorInt int endColor, int duration) {
    ValueAnimator anim = new ValueAnimator();
    anim.setIntValues(startColor, endColor);
    anim.setEvaluator(new ArgbEvaluator());
    anim.addUpdateListener(
        new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator valueAnimator) {
            viewToAnimate.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
          }
        });
    anim.setDuration(duration);
    anim.start();
  }
}
