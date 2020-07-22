package com.example.nextstreet.compose;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import java.io.File;

public interface CameraLauncher {
    static final String photoFileName = "photo.jpg";
    File launchCamera();
    File getPhotoFileUri(String photoFileName);
    File writeResizedBitmap(String photoFileName, Bitmap changedBitmap, String resized);
    Bitmap loadFromUri(Context context, Uri photoUri);
}
