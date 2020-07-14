package com.example.nextstreet.models;

import com.parse.ParseClassName;

@ParseClassName("Package")
public class PackageRequest {

    public static final String TAG = PackageRequest.class.getSimpleName();
    public static final String KEY_USER = "user";
    public static final String KEY_DRIVER = "driver";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ORIGIN = "origin";
    public static final String KEY_DESTINATION = "destination";

    // Set up empty constructor to register as ParseObject subclass
    public PackageRequest(){}
}
