package com.example.nextstreet.compose;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nextstreet.R;
import com.example.nextstreet.home.MapsPlaceSelectionResponder;
import com.example.nextstreet.home.NewSubmissionListener;
import com.example.nextstreet.models.PackageRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class ComposeHelper implements ThreadCompleteListener, PackageSubmissionResponder {

  private static final String TAG = ComposeHelper.class.getSimpleName();

  private static NewSubmissionListener newSubmissionListener;
  private static ParseUser minDriver;

  private com.example.nextstreet.databinding.FragmentComposeDetailsBinding binding;
  private Context context;

  private File photoFile;
  private LatLng dest;
  private LatLng origin;
  private Place destPlace;
  private Place originPlace;

  public static void addNewSubmissionListener(NewSubmissionListener newSubmissionListener) {
    ComposeHelper.newSubmissionListener = newSubmissionListener;
  }

  public static void removeNewSubmissionListener() {
    ComposeHelper.newSubmissionListener = null;
  }

  void setPhotoFile(File photoFile) {
    this.photoFile = photoFile;
  }

  void setOriginPlace(Place origin) {
    this.origin = origin.getLatLng();
    originPlace = origin;
  }

  void setDestinationPlace(Place destination) {
    this.dest = destination.getLatLng();
    this.destPlace = destination;
  }

  ComposeHelper(com.example.nextstreet.databinding.FragmentComposeDetailsBinding binding, Context context) {
    minDriver = null;
    this.binding = binding;
    this.context = context;
  }

  void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState, Fragment targetFragment) {
    MapsPlaceSelectionResponder mapsFragment = (MapsPlaceSelectionResponder) targetFragment;
    Preconditions.checkNotNull(mapsFragment);
    dest = mapsFragment.getDestination();

    if (dest != null) {
      binding.chooseDestinationTextView.setText(String.format("%s: %s", context.getText(R.string.destination), dest.toString()));
    }

    destPlace = mapsFragment.getDestinationPlace();
    originPlace = mapsFragment.getOriginPlace();

    if (destPlace != null) {
      binding.chooseDestinationTextView.setText(String.format("%s: %s", context.getText(R.string.destination), destPlace.getName()));
    }
    if (originPlace != null) {
      binding.chooseOriginTextView.setText(String.format("%s: %s", context.getText(R.string.origin), originPlace.getName()));
    }

    if (ParseUser.getCurrentUser() != null) {
      binding.submitButton.setOnClickListener(
              new PackageSubmissionOnClickListener(ParseUser.getCurrentUser().getUsername(), this));
    }
  }

  @Override
  public void checkPostable() {
    binding.pbLoading.setVisibility(ProgressBar.VISIBLE);

    String desc = binding.etDescription.getText().toString();

    Log.d(TAG, "checkPostable: destination =" + dest);
    Log.d(TAG, "checkPostable: origin" + origin);

    if (dest == null || origin == null) {
      Snackbar.make(binding.getRoot(), R.string.toast_dest_empt, Snackbar.LENGTH_SHORT).show();
      binding.pbLoading.setVisibility(ProgressBar.INVISIBLE);
    } else {
      if (desc.isEmpty()) {
        Snackbar.make(binding.getRoot(), R.string.toast_desc_empt, Snackbar.LENGTH_LONG).show();
      } else if (photoFile == null || binding.packageImageView.getDrawable() == null) {
        Snackbar.make(binding.getRoot(), R.string.toast_img_empt, Snackbar.LENGTH_LONG).show();
      }
      ParseUser currUser = ParseUser.getCurrentUser();
      saveRequest(desc, photoFile, currUser);
    }
  }

  private PackageRequest mostRecentRequest;

  private void saveRequest(String desc, File file, ParseUser currUser) {
    mostRecentRequest = new PackageRequest(file, desc, origin, dest, originPlace, destPlace, currUser);

    if (currUser != null) {
      mostRecentRequest.saveInBackground(
              new SaveCallback() {
                @Override
                public void done(ParseException e) {
                  binding.pbLoading.setVisibility(ProgressBar.INVISIBLE);

                  if (e != null) {
                    Log.e(TAG, "done: Error while saving request", e);
                    Snackbar.make(
                            binding.getRoot(), context.getString(R.string.toast_save_err), Snackbar.LENGTH_LONG)
                            .show();
                  } else {
                    Log.i(TAG, "done: Request save was successful!");
                    Snackbar.make(
                            binding.getRoot(), context.getString(R.string.toast_save_succ), Snackbar.LENGTH_LONG)
                            .show();
                    notifyNewSubmissionListeners();
                    queryAvailableDrivers();
                  }
                }
              });
    }
  }

  private void notifyNewSubmissionListeners() {
    ComposeHelper.newSubmissionListener.respondToNewSubmission(mostRecentRequest);
  }

  private static final String KEY_ISDRIVER = "isDriver";
  private static final String KEY_ISAVAILABLE = "isAvailable";
  private static final String KEY_HOME = "home";
  private static final int LIMIT_QUERY = 5;

  // error handling with getting drivers and making home in drivers
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
          Log.e(TAG, "queryPosts: Issue getting drivers", e);
        }

        DriverDistanceRunnable driverDistanceRunnable =
            new DriverDistanceRunnable(origin, dest, binding.getRoot(), drivers);

        driverDistanceRunnable.addListener(ComposeHelper.this);

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

    // TODO: Set this up so that driver has to accept or reject
    mostRecentRequest.put(PackageRequest.KEY_DRIVER, driver);
    mostRecentRequest.saveInBackground(
        new SaveCallback() {
          @Override
          public void done(ParseException e) {
            if (e != null) {
              Log.e(TAG, "done: Error while saving request", e);
              Snackbar.make(
                      binding.getRoot(), context.getString(R.string.toast_save_err), Snackbar.LENGTH_LONG)
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
    ComposeHelper.minDriver = minDriver;
  }
}
