package com.example.nextstreet.models;

import android.text.format.DateUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

@ParseClassName("PackageRequest")
public class PackageRequest extends ParseObject {

  public static final String TAG = PackageRequest.class.getSimpleName();
  public static final String KEY_USER = "user";
  public static final String KEY_DRIVER = "driver";
  public static final String KEY_IMAGE = "image";
  public static final String KEY_DESCRIPTION = "description";
  public static final String KEY_ORIGIN = "origin";
  public static final String KEY_DESTINATION = "destination";
  public static final String KEY_ORIGINPLACEID= "originPlaceId";
  public static final String KEY_DESTINATIONPLACEID= "destinationPlaceId";
  public static final String KEY_CREATEDAT = "createdAt";
  public static final String KEY_ISFULFILLED = "isFulfilled";
  public static final String KEY_ISDONE = "isDone";

  // Set up empty constructor to register as ParseObject subclass
  public PackageRequest() {}

  public PackageRequest(
      @Nullable File image,
      @Nullable String description,
      LatLng origin,
      LatLng destination,
      Place originPlace,
      Place destPlace,
      ParseUser user) {
    if (image != null) {
      put(KEY_IMAGE, image);
    }
    if (description != null) {
      put(KEY_DESCRIPTION, description);
    }
    put(KEY_ORIGIN, new ParseGeoPoint(origin.latitude, origin.longitude));
    ParseGeoPoint destGeoPoint = new ParseGeoPoint(destination.latitude, destination.longitude);
    ArrayList<ParseGeoPoint> geoPointArrayList = new ArrayList<ParseGeoPoint>();
    geoPointArrayList.add(destGeoPoint);
    put(KEY_DESTINATION, geoPointArrayList);

    if (originPlace != null) {
      put(KEY_ORIGINPLACEID, originPlace.getId());
    }
    if (destPlace != null) {
      put(KEY_DESTINATIONPLACEID, destPlace.getId());
    }

    put(KEY_USER, user);
  }

  public String getOriginPlaceId() {
    return getString(KEY_ORIGINPLACEID);
  }

  public String getDestinationPlaceId() {
    return getString(KEY_DESTINATIONPLACEID);
  }

  public ParseGeoPoint getOrigin() {
    return getParseGeoPoint(KEY_ORIGIN);
  }

  public ParseGeoPoint getDestination() {
    List<ParseGeoPoint> destination = (ArrayList<ParseGeoPoint>) get(KEY_DESTINATION);

    if (destination == null) {
      put(KEY_DESTINATION, new ArrayList<ParseGeoPoint>());
      throw new IllegalStateException(TAG + "getDestination: destination list should not be null");
    } else if (destination.size() == 0) {
      throw new IllegalStateException(TAG + "getDestination: destination list should not be empty");
    }

    return destination.get(0);
  }

  public String getRelativeTimeAgo() {
    return getRelativeTime(getCreatedAt());
  }

  private String getRelativeTime(Date date) {
    String relativeDate = "";
    long dateMillis = date.getTime();
    relativeDate =
        DateUtils.getRelativeTimeSpanString(
                dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
            .toString();

    return relativeDate;
  }

  public String getDescription() {
    return getString(KEY_DESCRIPTION);
  }

  public ParseUser getDriver() {
    return getParseUser(KEY_DRIVER);
  }

  public ParseFile getImage() {
    return getParseFile(KEY_IMAGE);
  }
}
