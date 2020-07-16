package com.example.nextstreet.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.io.File;


public class CameraOnClickListener implements View.OnClickListener{

    private String TAG;
    private Activity activity;
    private Fragment fragment;
    private String photoFileName;
    private File photoFile;

    public CameraOnClickListener(String TAG, Activity activity,
                                 Fragment fragment, String photoFileName) {
        this.TAG = TAG;
        this.activity = activity;
        this.fragment = fragment;
        this.photoFileName = photoFileName;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: camera button was clicked by user");
        photoFile =
                BitmapManipulation.launchCamera(activity, fragment,
                        photoFileName, TAG);
    }

    public File getPhotoFile() {
        return photoFile;
    }
}