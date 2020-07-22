package com.example.nextstreet.compose;

import android.util.Log;
import android.view.View;

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
