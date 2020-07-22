package com.example.nextstreet.ui.home.compose;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
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
    binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);

    binding.getRoot().setVisibility(View.INVISIBLE);

    binding.getRoot().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @TargetApi(Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                 int oldLeft, int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);
        setUpForShowAnimation(binding.getRoot());
      }
    });

    return binding.getRoot();
  }

  private void setUpForShowAnimation(final View root) {
    Log.i(TAG, "setUpForShowAnimation: ");
    AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
    builder.setView(root)
            .setCancelable(false);

    Dialog dialog = getDialog();

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    dialog.show();
    animateShowingFragment(root);
  }

  /**
   * A method where a view is revealed circularly from the bottom left corner.
   * @param viewToAnimate, the view to animate and reveal
   */
  private void animateShowingFragment(View viewToAnimate) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Log.i(TAG, "animateShowingFragment: animating");

      // get the center for the clipping circle
      int cx = viewToAnimate.getWidth();
      int cy = viewToAnimate.getHeight();

      // get the final radius for the clipping circle
      float finalRadius = (float) Math.hypot(cx, cy);

      // create the animator for this view (the start radius is zero)
      Animator anim = ViewAnimationUtils.createCircularReveal(viewToAnimate, cx, cy, 0f, finalRadius);
      anim.setDuration(getResources().getInteger(R.integer.composeFragment_time_appearing));
      anim.setInterpolator(new FastOutSlowInInterpolator());

      startColorAnimation(viewToAnimate, getResources().getColor(R.color.animation_start_color),
              getResources().getColor(R.color.animation_end_color), getResources().getInteger(R.integer.composeFragment_time_appearing));
      // make the view visible and start the animation
      viewToAnimate.setVisibility(View.VISIBLE);
      anim.start();

    } else {
      // set the view to invisible without a circular reveal animation below Lollipop
      viewToAnimate.setVisibility(View.VISIBLE);
    }
  }

  /**
   * A method to animate a growing circle during reveal transitions.
   * @param viewToAnimate, the view to animate is set final since it is accessed in inner class
   * @param startColor, start color for animation
   * @param endColor, ending color for animation
   * @param duration, length of animation
   */
  private void startColorAnimation(final View viewToAnimate, @ColorInt int startColor,
                                   @ColorInt int endColor, int duration) {
    ValueAnimator anim = new ValueAnimator();
    anim.setIntValues(startColor, endColor);
    anim.setEvaluator(new ArgbEvaluator());
    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        viewToAnimate.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
      }
    });
    anim.setDuration(duration);
    anim.start();
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
