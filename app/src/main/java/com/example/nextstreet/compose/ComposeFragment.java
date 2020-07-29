package com.example.nextstreet.compose;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
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
import androidx.lifecycle.ViewModelProviders;

import com.example.nextstreet.R;
import com.example.nextstreet.databinding.FragmentComposeBinding;
import com.example.nextstreet.home.HomeFragment;
import com.example.nextstreet.models.PackageRequest;
import com.example.nextstreet.utilities.CircularRevealDialogFragment;
import com.example.nextstreet.utilities.DismissOnClickListener;
import com.example.nextstreet.utilities.TextObserver;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends CircularRevealDialogFragment implements CameraLauncher, ThreadCompleteListener {

  private static final String TAG = ComposeFragment.class.getSimpleName();

  private static ParseUser minDriver;

  private FragmentComposeBinding binding;
  private ComposeViewModel composeViewModel;
  //TODO: make sure compose fragment does change appearance after submitting

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
    minDriver = null;
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    composeViewModel = ViewModelProviders.of(this).get(ComposeViewModel.class);
    binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);

    binding.getRoot().setVisibility(View.INVISIBLE);
    super.setUpOnLayoutListener(binding.getRoot(), true);

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

  private PackageRequest mostRecentRequest;

  private void saveRequest(String desc, File file, ParseUser currUser) {
        mostRecentRequest = new PackageRequest(file, desc, origin, dest, currUser);

        mostRecentRequest.saveInBackground(
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
                  queryAvailableDrivers();
                }
              }
            });
  }

  private static final String KEY_ISDRIVER = "isDriver";
  private static final String KEY_ISAVAILABLE = "isAvailable";
  private static final String KEY_HOME = "home";
  private static final int LIMIT_QUERY = 5;

  private void queryAvailableDrivers() {
    ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
    query.include(KEY_HOME);
    query.include(KEY_ISAVAILABLE);

    Log.d(TAG, "queryAvailableDrivers: querying");
    query.whereEqualTo(KEY_ISDRIVER, true);
    query.whereEqualTo(KEY_ISAVAILABLE, true);
    query.whereNear(KEY_HOME, new ParseGeoPoint(origin.latitude, origin.longitude));
    query.setLimit(LIMIT_QUERY);
    query.findInBackground(new DriverQueryCallback());
  }

  private class DriverQueryCallback implements FindCallback<ParseUser> {

    @Override
    public void done(List<ParseUser> drivers, ParseException e) {
      if (drivers != null) {
        Log.d(TAG, "done query: drivers size = " + drivers.size());
        if (e != null) {
          Log.e(TAG,  "queryPosts: Issue getting drivers", e);
        }

        DriverDistanceRunnable driverDistanceRunnable
                = new DriverDistanceRunnable(origin, dest, binding.getRoot(), drivers);

        driverDistanceRunnable.addListener(ComposeFragment.this);

        HandlerThread handlerThread = new HandlerThread("HandlerThreadName");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(driverDistanceRunnable);
      }
    }
  }

  // When Driver Distance Runnable Thread is done
  @Override
  public void notifyOfThreadComplete(Runnable runnable, final ParseUser driver) {
    Log.i(TAG, "notifyOfThreadComplete: minDriver = " + driver.getUsername() + " " + minDriver);

    //TODO: Set this up so that driver has to accept or reject
    mostRecentRequest.put(PackageRequest.KEY_DRIVER, driver);
    mostRecentRequest.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, "done: Error while saving request", e);
          Snackbar.make(
                  binding.getRoot(), getString(R.string.toast_save_err),
                  Snackbar.LENGTH_LONG)
                  .show();

        } else {
          Log.i(TAG, "done: Request save was successful!");
        }
      }
    });

    // TODO: update availability on driver's end, when driver is logged in
  }

  static void setMinDriver(ParseUser minDriver) {
    Log.i(TAG, "setMinDriver: minDriver = " + minDriver.getUsername() + " " + minDriver);
    ComposeFragment.minDriver = minDriver;
  }

  // TODO: fix crashing bug when submitting with camera!!!!
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

        photoFile = writeResizedBitmap(photoFileName, resizedBitmap, "resized");
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
