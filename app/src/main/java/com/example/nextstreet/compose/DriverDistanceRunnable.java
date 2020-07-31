package com.example.nextstreet.compose;

import android.util.Log;
import android.view.View;

import com.example.nextstreet.BuildConfig;
import com.example.nextstreet.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DriverDistanceRunnable implements Runnable {

  private static final String TAG = DriverDistanceRunnable.class.getSimpleName();
  private static final String KEY_HOME = "home";
  private final Set<ThreadCompleteListener> listeners =
      new CopyOnWriteArraySet<ThreadCompleteListener>();

  private LatLng dest;
  private LatLng origin;
  private View mainView; // this is for making Snackbar notifications during errors
  private List<ParseUser> drivers;
  private ParseUser minDriver;

  DriverDistanceRunnable(LatLng origin, LatLng dest, View mainView, List<ParseUser> drivers) {
    this.origin = origin;
    this.dest = dest;
    this.mainView = mainView;
    this.drivers = drivers;
  }

  public void addListener(final ThreadCompleteListener listener) {
    listeners.add(listener);
  }

  public void removeListener(final ThreadCompleteListener listener) {
    listeners.remove(listener);
  }

  private void notifyListeners() {
    for (ThreadCompleteListener listener : listeners) {
      Log.i(TAG, "notifyListeners: notifying " + listener.getClass().getSimpleName());
      listener.notifyOfThreadComplete(this, minDriver);
    }
  }

  @Override
  public final void run() {
    try {
      doRun();
    } finally {
      notifyListeners();
    }
  }

  public void doRun() {
    Log.d(TAG, "doRun: running");
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

    HashMap<ParseUser, Integer> distances = new HashMap<>();
    for (ParseUser driver : drivers) {
      Log.d(TAG, "doRun: " + driver);
      ParseGeoPoint currDriverLocation = (ParseGeoPoint) driver.get(KEY_HOME);
      Preconditions.checkNotNull(currDriverLocation);
      int distance =
          getDistances(
              new LatLng(currDriverLocation.getLatitude(), currDriverLocation.getLongitude()),
              origin);
      distances.put(driver, distance);
    }

    for (ParseUser driver : drivers) {
      Log.i(
          TAG,
          "done: received distances = " + distances.get(driver) + " from " + driver.getUsername());
    }

    int minimum = Integer.MAX_VALUE;
    ParseUser minDriver = drivers.get(0);
    for (ParseUser driver : drivers) {
      int distance = distances.get(driver);
      if (distance < minimum) {
        minimum = distance;
        minDriver = driver;
      }
    }

    Log.i(TAG, "run: min driver = " + minDriver.getUsername());

    ComposeFragment.setMinDriver(minDriver);
    this.minDriver = minDriver;
  }

  // TODO: Set up a separate thread to run this code, then use okhttp synchronous
  private static final String KEY_ROWS_JSON_ARRAY = "rows";
  private static final String KEY_ELEMENTS_JSON_ARRAY = "elements";
  private static final String KEY_DURATION_ELEMENT =
      "duration_in_traffic"; // can also be just "duration" or "distance"
  private static final String KEY_VALUE = "value";
  private static final int ERROR_DISTANCE = -100;

  // Todo; make a more complex algorithm taking into consideration entire length of
  private int getDistances(LatLng start, LatLng end) {
    OkHttpClient client = new OkHttpClient();

    String url =
        "https://maps.googleapis.com/maps/api/distancematrix/json?"
            + "origins="
            + start.latitude
            + ","
            + start.longitude
            + "&destinations="
            + end.latitude
            + ","
            + end.longitude
            + "&departure_time=now"
            + "&key="
            + BuildConfig.MAPS_API_KEY;

    Log.d(TAG, "getDistances: " + url);

    final Request request = new Request.Builder().url(url).build();

    int distance = ERROR_DISTANCE;
    try {
      Response response = client.newCall(request).execute();
      distance = driverDistanceRequestResponse(response);
    } catch (IOException e) {
      Snackbar.make(mainView, R.string.toast_http_err, Snackbar.LENGTH_LONG).show();
      Log.e(TAG, "onFailure: http request to Distance Matrix API failed with error", e);
    }

    return distance;
  }

  // TODO: Figure out if can get API exception and status
  private int driverDistanceRequestResponse(Response response) throws IOException {
    if (!response.isSuccessful()) {
      Log.d(TAG, "onResponse: Response was unexpectedly not successful with " + response);
      Snackbar.make(mainView, R.string.toast_http_err, Snackbar.LENGTH_LONG).show();
      throw new IOException("Unexpected code " + response);
    } else {
      Log.i(TAG, "onResponse: result = " + " response = " + response);
      Headers responseHeaders = response.headers();
      for (int i = 0; i < responseHeaders.size(); i++) {
        Log.d("DEBUG", responseHeaders.name(i) + ":" + responseHeaders.value(i));
      }

      // TODO: move to GSON
      return getDistanceFromJSON(response);
    }
  }

  private int getDistanceFromJSON(Response response) {
    try {
      String responseData = response.body().string();
      JSONObject json = new JSONObject(responseData);
      Log.d(TAG, "onResponse: " + json);
      JSONArray rows = json.getJSONArray(KEY_ROWS_JSON_ARRAY);
      JSONObject rowObject = (JSONObject) rows.get(0);
      JSONArray elements = (JSONArray) rowObject.get(KEY_ELEMENTS_JSON_ARRAY);
      // TODO: consider using different values from array (distance, duration, duration in traffic)
      JSONObject distances = (JSONObject) elements.get(0);
      JSONObject trueDuration = (JSONObject) distances.get(KEY_DURATION_ELEMENT);
      int distance = trueDuration.getInt(KEY_VALUE);
      Log.i(TAG, "getDistanceFromJSON: distance = " + distance);
      return distance;
    } catch (JSONException | IOException e) {
      Snackbar.make(mainView, R.string.toast_json_err, Snackbar.LENGTH_LONG).show();
      Log.e(TAG, "onResponse: error processing json response", e);
      return ERROR_DISTANCE;
    }
  }
}
