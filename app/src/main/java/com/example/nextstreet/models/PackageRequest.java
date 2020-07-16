package com.example.nextstreet.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;

@ParseClassName("PackageRequest")
public class PackageRequest extends ParseObject {

    public static final String TAG = PackageRequest.class.getSimpleName();
    public static final String KEY_USER = "user";
    public static final String KEY_DRIVER = "driver";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ORIGIN = "origin";
    public static final String KEY_DESTINATION = "destination";
    public static final String KEY_CREATEDAT = "createdAt";

    // Set up empty constructor to register as ParseObject subclass
    public PackageRequest(){}

    public PackageRequest(File image, String description,
                          LatLng origin, LatLng destination, ParseUser user){
        if (image != null) {
            put(KEY_IMAGE, image);
        }
        if (description != null) {
            put(KEY_DESCRIPTION, description);
        }
        put(KEY_ORIGIN, new ParseGeoPoint(origin.latitude, origin.longitude));
        ParseGeoPoint destGeoPoint= new ParseGeoPoint(destination.latitude, destination.longitude);
        ArrayList<ParseGeoPoint> geoPointArrayList = new ArrayList<ParseGeoPoint>();
        geoPointArrayList.add(destGeoPoint);
        put(KEY_DESTINATION, geoPointArrayList);
        put(KEY_USER, user);
    }

    public ParseGeoPoint getOrigin() {
        return getParseGeoPoint(KEY_ORIGIN);
    }

    public ParseGeoPoint getDestination() {
        ArrayList<ParseGeoPoint> destination = (ArrayList<ParseGeoPoint>) get(KEY_DESTINATION);
        return destination.get(0);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
}
