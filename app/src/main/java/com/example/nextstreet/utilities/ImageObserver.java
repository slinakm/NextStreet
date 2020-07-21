package com.example.nextstreet.utilities;

import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageObserver implements Observer<File> {
  private final ImageView imageViewToObserve;
  private final Activity activity;
  private final RequestOptions requestOptions;

  public ImageObserver(ImageView imageViewToObserve, Activity activity, @Nullable RequestOptions requestOptions) {
    this.imageViewToObserve = imageViewToObserve;
    this.activity = activity;
    this.requestOptions = requestOptions;
  }

  @Override
  public void onChanged(@Nullable File f) {
    if (requestOptions == null) {
      Glide.with(activity).load(f).into(imageViewToObserve);
    } else {
      Glide.with(activity).load(f).apply(requestOptions).into(imageViewToObserve);
    }
  }
}
