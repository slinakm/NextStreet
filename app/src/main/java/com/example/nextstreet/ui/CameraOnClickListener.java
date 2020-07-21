package com.example.nextstreet.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.io.File;

public class CameraOnClickListener implements View.OnClickListener {

  private static final String TAG = CameraOnClickListener.class.getSimpleName();
  private final Activity activity;
  private final Fragment fragment;
  private final String photoFileName;
  private File photoFile;

  public CameraOnClickListener(Activity activity, Fragment fragment, String photoFileName) {
    this.activity = activity;
    this.fragment = fragment;
    this.photoFileName = photoFileName;
  }

  @Override
  public void onClick(View view) {
    Log.i(TAG, "onClick: camera button was clicked by user");
    photoFile = BitmapManipulation.launchCamera(activity, fragment, photoFileName, TAG);
  }

  public File getPhotoFile() {
    return photoFile;
  }
}
