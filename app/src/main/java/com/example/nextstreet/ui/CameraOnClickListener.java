package com.example.nextstreet.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;

public class CameraOnClickListener implements View.OnClickListener {

  private static final String TAG = CameraOnClickListener.class.getSimpleName();
  private final CameraLauncher cameraLauncher;
  private File photoFile;

  public CameraOnClickListener(CameraLauncher cameraLauncher) {
    this.cameraLauncher = cameraLauncher;
  }

  @Override
  public void onClick(View view) {
    Log.i(TAG, "onClick: camera button was clicked by user");
    photoFile = cameraLauncher.launchCamera();
  }

  public File getPhotoFile() {
    return photoFile;
  }
}
