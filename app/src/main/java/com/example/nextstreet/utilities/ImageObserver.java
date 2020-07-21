package com.example.nextstreet.utilities;

import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageObserver implements Observer<File> {
  private ImageView iv;
  private Activity activity;
  private RequestOptions requestOptions;

  public ImageObserver(ImageView iv, Activity activity, RequestOptions requestOptions) {
    this.iv = iv;
    this.activity = activity;
    this.requestOptions = requestOptions;
  }

  @Override
  public void onChanged(@Nullable File f) {
    Glide.with(activity).load(f).apply(requestOptions).into(iv);
  }
}
