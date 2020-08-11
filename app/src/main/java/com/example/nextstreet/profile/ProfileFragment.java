package com.example.nextstreet.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.nextstreet.R;
import com.example.nextstreet.compose.BitmapManipulation;
import com.example.nextstreet.compose.CameraLauncher;
import com.example.nextstreet.compose.CameraOnClickListener;
import com.example.nextstreet.databinding.FragmentProfileBinding;
import com.example.nextstreet.utilities.CircularRevealDialogFragment;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends CircularRevealDialogFragment implements CameraLauncher {

  private static final String TAG = ProfileFragment.class.getSimpleName();
  private static final String PROFILE_PIC = "profilePic";

  private FragmentProfileBinding binding;
  private CameraOnClickListener cameraOnClickListener;

  public static ProfileFragment newInstance() {

    Bundle args = new Bundle();

    ProfileFragment fragment = new ProfileFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProfileBinding.inflate(getLayoutInflater());
    setUpOnLayoutListener(binding.getRoot(), true);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    cameraOnClickListener = new CameraOnClickListener(this);

    binding.logoutButton.setOnClickListener(new LogoutOnClickListener(getActivity()));
    binding.profilePictureButton.setOnClickListener(cameraOnClickListener);
    binding.ivCancel.setOnClickListener(new DismissOnClickListener(this));
    Glide.with(getContext()).load(R.mipmap.ic_launcher_round).into(binding.profilePictureImageView);
  }

  @Override
  public void onResume() {
    super.onResume();

    getDialog()
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
  }

  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.i(TAG, "onActivityResult: Activity done.");

    if (requestCode == BitmapManipulation.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      File tempPhotoFile = cameraOnClickListener.getPhotoFile();
      Log.i(TAG, "onActivityResult: taking photo");

      if (resultCode == RESULT_OK && tempPhotoFile != null) {
        Log.i(TAG, "onActivityResult: took photo");
        Bitmap takenImage = BitmapFactory.decodeFile(tempPhotoFile.getAbsolutePath());

        Bitmap resizedBitmap =
                BitmapManipulation.scaleToFitWidth(
                        takenImage, (int) getResources().getDimension((R.dimen.resized_post_image)));

        binding.profileHeaderImageView.setImageBitmap(resizedBitmap);

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put(PROFILE_PIC, takenImage);
        currentUser.saveInBackground();
      }
    }
  }

    @Override
  public File launchCamera() {
    Log.i(TAG, "launchCamera: ");
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File photoFile = getPhotoFileUri(photoFileName);

    Uri fileProvider =
            FileProvider.getUriForFile(getContext(), getString(R.string.file_authority), photoFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
      startActivityForResult(intent, BitmapManipulation.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    return photoFile;
  }

  @Override
  public File getPhotoFileUri(String photoFileName) {
    File mediaStorageDir =
            new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
      Log.d(TAG, "getPhotoFileUri: failed to create directory");
    }

    return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
  }

  @Override
  public File writeResizedBitmap(String photoFileName, Bitmap changedBitmap, String resized) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    changedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

    String filename = photoFileName + "_" + resized;
    File resizedFile = getPhotoFileUri(filename);

    try {
      resizedFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(resizedFile);
      fos.write(bytes.toByteArray());
      fos.close();
    } catch (IOException e) {
      Log.e(TAG, "writeResizedBitmap: error writing image to new file", e);
    }
    return resizedFile;
  }

  @Override
  public Bitmap loadFromUri(Context context, Uri photoUri) {
    Bitmap image = null;
    try {
      // check version of Android on device
      if (Build.VERSION.SDK_INT > 27) {
        ImageDecoder.Source source =
                ImageDecoder.createSource(context.getContentResolver(), photoUri);
        image = ImageDecoder.decodeBitmap(source);
      } else {
        // support older versions of Android by using getBitmap
        image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
      }
    } catch (IOException e) {
      Log.e(TAG, "loadFromUri: error loading image from file", e);
    }
    return image;
  }
}
