package com.example.nextstreet.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.File;

@ParseClassName("PackageRequest")
public class PackageRequest extends ParseObject {

    public static final String TAG = PackageRequest.class.getSimpleName();
    public static final String KEY_USER = "user";
    public static final String KEY_DRIVER = "driver";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ORIGIN = "origin";
    public static final String KEY_DESTINATION = "destination";

    // Set up empty constructor to register as ParseObject subclass
    public PackageRequest(){}

    public PackageRequest(File image, String description,
                          LatLng origin, LatLng destination){
        put(KEY_IMAGE, image);
        put(KEY_DESCRIPTION, description);
        put(KEY_ORIGIN, origin);
        put(KEY_DESTINATION, destination);
    }
}
