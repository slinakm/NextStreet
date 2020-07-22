package com.example.nextstreet.ui.home.compose;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.ui.BitmapManipulation;
import com.example.nextstreet.ui.CameraLauncher;
import com.example.nextstreet.ui.CameraOnClickListener;
import com.example.nextstreet.ui.home.HomeFragment;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.example.nextstreet.utilities.ImageObserver;
import com.example.nextstreet.utilities.TextObserver;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends DialogFragment implements CameraLauncher {

  private static final String TAG = ComposeFragment.class.getSimpleName();

  private FragmentComposeBinding binding;
  private ComposeViewModel composeViewModel;

  private CameraOnClickListener cameraOnClickListener;
  private File photoFile;
  private LatLng dest;
  private LatLng origin;

  public static ComposeFragment newInstance() {
    Bundle args = new Bundle();

    ComposeFragment fragment = new ComposeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    composeViewModel = ViewModelProviders.of(this).get(ComposeViewModel.class);
    binding = FragmentComposeBinding.inflate(getLayoutInflater());

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    composeViewModel
        .getDescription()
        .observe(getViewLifecycleOwner(), new TextObserver(binding.etDescription));

    dest = HomeFragment.getDestination();
    origin = HomeFragment.getOrigin();
    if (dest != null) {
      binding.destinationTextView.setText(dest.toString());
    }
    if (origin != null) {
      binding.originTextView.setText(origin.toString());
    }

    binding.ivCancel.setOnClickListener(new DismissOnClickListener(this));
    cameraOnClickListener = new CameraOnClickListener(this);
    binding.cameraButton.setOnClickListener(cameraOnClickListener);
    binding.submitButton.setOnClickListener(
        new PackageSubmissionOnClickListener(ParseUser.getCurrentUser().getUsername(), this));
  }

  @Override
  public void onResume() {
    super.onResume();

    getDialog()
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
  }

  void checkPostable() {
    binding.pbLoading.setVisibility(ProgressBar.VISIBLE);

    String desc = binding.etDescription.getText().toString();

    Log.d(TAG, "checkPostable: destination =" + dest);
    Log.d(TAG, "checkPostable: origin" + origin);

    if (dest == null || origin == null) {
      Toast.makeText(getContext(), R.string.toast_dest_empt, Toast.LENGTH_SHORT).show();
      binding.pbLoading.setVisibility(ProgressBar.INVISIBLE);
    } else {
      if (desc.isEmpty()) {
        Snackbar.make(binding.getRoot(), R.string.toast_desc_empt, Snackbar.LENGTH_LONG).show();
      } else if (photoFile == null || binding.ivPackage.getDrawable() == null) {
        Snackbar.make(binding.getRoot(), R.string.toast_img_empt, Snackbar.LENGTH_LONG).show();
      }
      ParseUser currUser = ParseUser.getCurrentUser();
      saveRequest(desc, photoFile, currUser);
    }
  }

  private void saveRequest(String desc, File file, ParseUser currUser) {

    PackageRequest request = new PackageRequest(file, desc, origin, dest, currUser);

    request.saveInBackground(
        new SaveCallback() {
          @Override
          public void done(ParseException e) {
            binding.pbLoading.setVisibility(ProgressBar.INVISIBLE);

            if (e != null) {
              Log.e(TAG, "done: Error while saving request", e);
              Snackbar.make(
                      binding.getRoot(), getString(R.string.toast_save_err), Snackbar.LENGTH_LONG)
                  .show();

            } else {
              Log.i(TAG, "done: Request save was successful!");
              Snackbar.make(
                      binding.getRoot(), getString(R.string.toast_save_succ), Snackbar.LENGTH_LONG)
                  .show();
            }
          }
        });
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

        binding.ivPackage.setImageBitmap(resizedBitmap);
        binding.ivPackage.setVisibility(View.VISIBLE);

        photoFile =
                writeResizedBitmap(photoFileName, resizedBitmap, "resized");
      } else {
        Snackbar.make(
                binding.getRoot(),
                getString(R.string.toast_camera_err),
                BaseTransientBottomBar.LENGTH_SHORT)
                .show();
      }
    }
  }

  @Override
  public File getPhotoFileUri(String photoFileName) {
    File mediaStorageDir = new File(
            getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            TAG);

    if (!mediaStorageDir.exists()
            && !mediaStorageDir.mkdirs()) {
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
      if(Build.VERSION.SDK_INT > 27){
        ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
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
