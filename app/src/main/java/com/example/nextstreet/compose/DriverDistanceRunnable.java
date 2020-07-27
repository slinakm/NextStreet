package com.example.nextstreet.compose;

import android.util.Log;
import android.view.View;

import com.example.nextstreet.BuildConfig;
import com.example.nextstreet.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Driver;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DriverDistanceRunnable implements Runnable {

    private static final String TAG = DriverDistanceRunnable.class.getSimpleName();
    private static final String KEY_ISDRIVER = "isDriver";
    private static final String KEY_ISAVAILABLE = "isAvailable";
    private static final String KEY_HOME = "home";

    private LatLng dest;
    private LatLng origin;
    private View mainView; // this is for making Snackbar notifications during errors

    DriverDistanceRunnable(LatLng origin, LatLng dest, View mainView) {
        this.origin = origin;
        this.dest = dest;
        this.mainView = mainView;
    }

    @Override
    public void run() {
        queryAvailableDrivers();
    }

    void queryAvailableDrivers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include(KEY_HOME);

        Log.d(TAG, "queryAvailableDrivers: querying");
        query.whereEqualTo(KEY_ISDRIVER, true);
        query.whereEqualTo(KEY_ISAVAILABLE, true);
        //    query.whereWithinMiles(KEY_HOME, new ParseGeoPoint(origin.latitude, origin.longitude),
        //            getResources().getInteger(R.integer.range_of_drivers_miles));

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

                HashMap<ParseUser, Integer> distances = new HashMap<>();
                for (ParseUser driver : drivers) {
                    ParseGeoPoint currDriverLocation = (ParseGeoPoint) driver.get(KEY_HOME);
                    Preconditions.checkNotNull(currDriverLocation);
                    int distance = getDistances(
                            new LatLng(currDriverLocation.getLatitude(), currDriverLocation.getLongitude()),
                            origin);
                    distances.put(driver, distance);
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

                ComposeFragment.findDriver(minDriver);
            }
        }
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
                        + start.latitude
                        + ","
                        + start.longitude
                        + "&departure_time=now"
                        + "&key="
                        + BuildConfig.MAPS_API_KEY;

        Log.d(TAG, "getDistances: " + url);
        final Request request = new Request.Builder().url(url).build();

        client
                .newCall(request)
                .enqueue(new DriverDistanceCallback());
    }

    private class DriverDistanceCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            Snackbar.make(mainView, R.string.toast_http_err, Snackbar.LENGTH_LONG)
                    .show();
            Log.e(TAG, "onFailure: http request to Distance Matrix API failed with error", e);
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.d(
                        TAG, "onResponse: Response was unexpectedly not successful with " + response);
                Snackbar.make(mainView, R.string.toast_http_err, Snackbar.LENGTH_LONG)
                        .show();
                throw new IOException("Unexpected code " + response);
            } else {
                Snackbar.make(mainView, R.string.toast_driver_succ, Snackbar.LENGTH_LONG)
                        .show();
                Log.i(TAG, "onResponse: result = " + call.toString() + " response = " + response);
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.d("DEBUG", responseHeaders.name(i) + ":" + responseHeaders.value(i));
                }

                // TODO: move to GSON
                getDistanceFromJSON(response);
            }
        }
    }

    private int getDistanceFromJSON(Response response) {
        try {
            String responseData = response.body().string();
            JSONObject json = new JSONObject(responseData);
            Log.d(TAG, "onResponse: " + json);
            JSONArray rows = json.getJSONArray(KEY_ROWS_JSON_ARRAY);
            JSONArray elements = (JSONArray) rows.get(0);
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
