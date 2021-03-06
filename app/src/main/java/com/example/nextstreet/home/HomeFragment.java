package com.example.nextstreet.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nextstreet.BuildConfig;
import com.example.nextstreet.MainActivity;
import com.example.nextstreet.R;
import com.example.nextstreet.compose.ComposeDetailsFragment;
import com.example.nextstreet.compose.ComposeHelper;
import com.example.nextstreet.compose.FragmentCallback;
import com.example.nextstreet.compose.UsersFragment;
import com.example.nextstreet.databinding.BottomSheetComposeBinding;
import com.example.nextstreet.databinding.FragmentHomeBinding;
import com.example.nextstreet.models.PackageRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Preconditions;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Google maps manipulation based on https://developers.google.com/maps/documentation/android-sdk/
 * current-place-tutorial#get-the-location-of-the-android-device-and-position-the-map
 */
public class HomeFragment extends Fragment
    implements QueryResponder,
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        NewSubmissionListener,
        MapsPlaceSelectionResponder,
        FragmentCallback,
        CurrentRequestsAdapter.OnSingleClickRequestResponder{

  public static final int DEFAULT_ZOOM = 500;
  private static final String TAG = HomeFragment.class.getSimpleName();
  private static final String HOME_PLACE = "homePlaceId";


  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private static final int AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 2;
  private static final int MOST_RECENT_PACKAGE_REQUEST_CODE = 10;
  private static final int RECENT_PACKAGE_LIST_REQUEST_CODE = 10;

  private static final String NOTIFICATION_CHANNEL_ID = "NextStreet_Channel";
  private static final int NOTIFICATION_NEW_DRIVER_ID = 22;
  private SubscriptionHandling<PackageRequest> subscriptionHandling;

  private static PackageRequest currRequest;

  private static Location lastKnownLocation;
  private LatLng destination;
  private LatLng origin;
  private Place destinationPlace;
  private Place originPlace;

  private FragmentHomeBinding binding;
  private BottomSheetComposeBinding bottomSheetComposeBinding;
  private boolean locationPermissionGranted;

  private boolean onCurrentRequest;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private GoogleMap map;

  private Marker markerOrigin;
  private Marker markerDestination;

  private RecyclerView currentPackagesRecyclerView;
  private CurrentRequestsAdapter adapter;

  static void setLastKnownLocation(Location lastKnownLocation) {
    HomeFragment.lastKnownLocation = lastKnownLocation;
  }

  @Override
  public LatLng getDestination() {
    return destination;
  }

  @Override
  public LatLng getOrigin() {
    if (lastKnownLocation != null) {
      origin = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }
    return origin;
  }

  @Override
  public Place getDestinationPlace() {
    return destinationPlace;
  }

  @Override
  public Place getOriginPlace() {
    return originPlace;
  }

  @Override
  public void setDestinationPlace(Place destinationPlace) {
    this.destinationPlace = destinationPlace;
  }

  @Override
  public void setOriginPlace(Place originPlace) {
    this.originPlace = originPlace;
  }

  public static boolean hasCurrRequest() {
    return currRequest != null;
  }

  public static PackageRequest getCurrRequest() {
    return currRequest;
  }

  @Override
  public void setOrigin(LatLng newPlace) {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace, HomeFragment.DEFAULT_ZOOM));
    origin = newPlace;
    Preconditions.checkNotNull(newPlace, "newPlace unexpectedly null");
    setMarkerOrigin(newPlace);
  }

  void setOriginNoCamera(LatLng newPlace) {
    origin = newPlace;
    Preconditions.checkNotNull(newPlace, "newPlace unexpectedly null");
    setMarkerOrigin(newPlace);
  }

  @Override
  public void setDestination(LatLng newPlace) {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace, HomeFragment.DEFAULT_ZOOM));
    destination = newPlace;
    Preconditions.checkNotNull(newPlace, "newPlace unexpectedly null");
    setMarkerDestination(newPlace);
  }

  private void setMarkerOrigin(LatLng latLng) {

    MarkerOptions marker =
        new MarkerOptions().position(latLng).title(getActivity().getString(R.string.origin)).flat(true);
    Marker markerOrigin = map.addMarker(marker);

    if (this.markerOrigin != null) {
      this.markerOrigin.remove();
    }
    this.markerOrigin = markerOrigin;
  }

  private void setMarkerDestination(LatLng latLng) {
    MarkerOptions marker =
        new MarkerOptions()
            .position(latLng)
            .title(getActivity().getString(R.string.destination))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .flat(true);
    Marker markerDestination = map.addMarker(marker);

    if (this.markerDestination != null) {
      this.markerDestination.remove();
    }
    this.markerDestination = markerDestination;
  }

  private void setOnCurrentRequestToFalse() {
    onCurrentRequest = false;
  }

  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(getLayoutInflater());
    bottomSheetComposeBinding = binding.layoutBottomSheet;

    SupportMapFragment mMapFragment =
            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    Log.i(TAG, "onCreateView: " + mMapFragment);
    Preconditions.checkNotNull(mMapFragment, "mMapFragment is unexpectedly null");
    mMapFragment.getMapAsync(this);

    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

    ComposeHelper.addNewSubmissionListener(this);
    createNotificationChannel();
    setUpListener();
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setUpCurrentRecyclerView();
    setUpBottomSheet();
  }

  private void setUpCurrentRecyclerView() {
    currentPackagesRecyclerView = binding.currentPackagesRecyclerView.currentRequestsRecyclerView;
    currentPackagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false) {
      @Override
      public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        lp.width = (int) (getWidth() / 1.5);
        return true;
      }
    });
    AppCompatActivity appCompatActivityOfThis = (AppCompatActivity) getActivity();
    adapter = new CurrentRequestsAdapter(appCompatActivityOfThis, new ArrayList<PackageRequest>(), this);
    currentPackagesRecyclerView.setAdapter(adapter);
    queryCurrentPackages();
  }

  private void setUpListener() {
    ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

    ParseQuery<PackageRequest> parseQuery = ParseQuery.getQuery(PackageRequest.class);
    parseQuery.include(PackageRequest.KEY_DRIVER);
    parseQuery.whereEqualTo(PackageRequest.KEY_USER, ParseUser.getCurrentUser());
    parseQuery.whereEqualTo(PackageRequest.KEY_ISDONE, false);
    parseQuery.whereEqualTo(PackageRequest.KEY_ISFULFILLED, true);

    subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

    subscriptionHandling.handleEvent(
            SubscriptionHandling.Event.ENTER,
            new SubscriptionHandling.HandleEventCallback<PackageRequest>() {
              @Override
              public void onEvent(ParseQuery<PackageRequest> query, PackageRequest requestReceived) {
                ParseUser driver = requestReceived.getParseUser(PackageRequest.KEY_DRIVER);
                Log.i(TAG, "onEvent: new package request was received with Driver " + driver);
                Preconditions.checkNotNull(requestReceived);
                queryCurrentPackages();
                createNotification();
              }
            });
  }

  private void createNotification() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationCompat.Builder driverFoundNotification =
              new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID)
                      .setSmallIcon(R.drawable.package_notification_icon)
                      .setContentTitle(getResources().getString(R.string.notification_newDriver_title))
                      .setContentText(getResources().getString(R.string.notification_newDriver_description))
                      .setPriority(NotificationCompat.PRIORITY_DEFAULT);
      NotificationManager mNotificationManager =
              (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

      mNotificationManager.notify(NOTIFICATION_NEW_DRIVER_ID, driverFoundNotification.build());
    }
  }

  /**
   * Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is
   * new and not in the support library.
   */
  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel =
              new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void queryCurrentPackages() {
    ParseQuery<PackageRequest> query = ParseQuery.getQuery(PackageRequest.class);
    query.orderByDescending(PackageRequest.KEY_CREATEDAT);
    query.include(PackageRequest.KEY_USER);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_DESCRIPTION);
    query.include(PackageRequest.KEY_ORIGIN);
    query.include(PackageRequest.KEY_DRIVER);
    query.include(PackageRequest.KEY_DESTINATION);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_ISFULFILLED);

    ParseUser currUser = ParseUser.getCurrentUser();
    Log.d(TAG, "queryCurrentPackage: currUser = " + currUser.getUsername());
    query.whereEqualTo(PackageRequest.KEY_USER, currUser);
    query.whereEqualTo(PackageRequest.KEY_ISDONE, false);

    query.findInBackground(new RequestQueryCallback(this, RECENT_PACKAGE_LIST_REQUEST_CODE));
  }

  private void setUpBottomSheet() {
    bottomSheetComposeBinding.bottomSheetTopPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (bottomSheetComposeBinding.bottomSheetCompose.getVisibility() == View.GONE) {
          bottomSheetComposeBinding.bottomSheetCompose.setVisibility(View.VISIBLE);
          Glide.with(getContext())
                  .load(getResources().getDrawable(R.drawable.ic_baseline_minimize_24))
                  .into(bottomSheetComposeBinding.minmaxImageView);
        } else {
          bottomSheetComposeBinding.bottomSheetCompose.setVisibility(View.GONE);
          Glide.with(getContext())
                  .load(getResources().getDrawable(R.drawable.ic_baseline_maximize_24))
                  .into(bottomSheetComposeBinding.minmaxImageView);
        }
      }
    });

    bottomSheetComposeBinding.toDestinationImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
      }
    });

    bottomSheetComposeBinding.toUsersImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FragmentManager fm = getChildFragmentManager();
        UsersFragment.display(fm);
      }
    });

    //TODO: make a new fragment with a map for users, get requests programatically
    // TODO: set up sliding for bottom sheet
    bottomSheetComposeBinding.nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FragmentManager fm = getParentFragmentManager();
        ComposeDetailsFragment.display(HomeFragment.this, fm);
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == AUTOCOMPLETE_DESTINATION_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Preconditions.checkNotNull(data);
        Place destinationPlace = Autocomplete.getPlaceFromIntent(data);
        Log.i(TAG, "Place: " + destinationPlace.getName() + ", " + destinationPlace.getId());

        setDestination(destinationPlace.getLatLng());
        setDestinationPlace(destinationPlace);
        bottomSheetComposeBinding.chooseDestinationTextView.setText(String.format("%s: %s", getResources().getText(R.string.destination), destinationPlace.getName()));

        setBottomVisible();
      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.e(TAG, status.getStatusMessage());
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void setBottomVisible() {
    bottomSheetComposeBinding.secondDivider.setVisibility(View.VISIBLE);
    bottomSheetComposeBinding.nextButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    if (map == null) {
      Log.e(TAG, "onMapReady: Error loading map, map was null");
      return;
    }
    map.setOnMapLongClickListener(this);
    queryMostRecentPackage();
  }

  /**
   * Request location permission, so that we can get the location of the device. The result of the
   * permission request is handled by a callback, onRequestPermissionsResult.
   */
  private void getLocationPermission() {
    if (ContextCompat.checkSelfPermission(
            getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      locationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(
          getActivity(),
          new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    locationPermissionGranted = false;
    if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) { // If request is cancelled, the result arrays are empty.
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        locationPermissionGranted = true;
      }
    }
    updateLocationUI();
  }

  private void updateLocationUI() {
    if (map == null) {
      return;
    }

    try {
      if (locationPermissionGranted) {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
      } else {
        map.setMyLocationEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        lastKnownLocation = null;
        getLocationPermission();
      }
    } catch (SecurityException e) {
      Log.e(TAG, "updateLocationUI: security exception getting map data", e);
    }
  }

  /**
   * Get the best and most recent location of the device, which may be null in rare cases when a
   * location is not available.
   */
  private void getDeviceLocation() {
    try {
      if (locationPermissionGranted) {
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new LocationResultOnCompleteListener(map, this));
      } else {
        Snackbar.make(
                binding.getRoot(),
                getActivity().getString(R.string.maps_no_permissions_err),
                Snackbar.LENGTH_SHORT)
            .show();
        Log.d(TAG, "getDeviceLocation: Location Permission not granted");
      }
    } catch (SecurityException e) {
      Log.e(TAG, "getDeviceLocation: security exception getting device location", e);
    }
  }

  @Override
  public void onMapLongClick(LatLng latLng) {
    if (!onCurrentRequest) {
      Snackbar.make(binding.getRoot(), getActivity().getString(R.string.set_destination), Snackbar.LENGTH_SHORT)
              .show();
      setDestination(latLng);
    }
  }

  private void queryMostRecentPackage() {
    ParseQuery<PackageRequest> query = ParseQuery.getQuery(PackageRequest.class);
    query.orderByDescending(PackageRequest.KEY_CREATEDAT);
    query.include(PackageRequest.KEY_USER);
    query.include(PackageRequest.KEY_IMAGE);
    query.include(PackageRequest.KEY_DESCRIPTION);
    query.include(PackageRequest.KEY_ORIGIN);
    query.include(PackageRequest.KEY_DRIVER);
    query.include(PackageRequest.KEY_DESTINATION);
    query.include(PackageRequest.KEY_ORIGINPLACEID);
    query.include(PackageRequest.KEY_DESTINATIONPLACEID);
    query.include(PackageRequest.KEY_ISFULFILLED);

    ParseUser currUser = ParseUser.getCurrentUser();
    Log.d(TAG, "queryMostRecentPackage: currUser = " + currUser.getUsername());
    query.whereEqualTo(PackageRequest.KEY_USER, currUser);
    query.whereEqualTo(PackageRequest.KEY_ISDONE, false);

    query.setLimit(3);
    query.findInBackground(new RequestQueryCallback(this, MOST_RECENT_PACKAGE_REQUEST_CODE));
  }

  @Override
  public void respondToQuery(List<PackageRequest> requests, int requestCode) {

    if (requestCode == MOST_RECENT_PACKAGE_REQUEST_CODE) {
      boolean currRequestExists = (requests != null && requests.size() != 0);

      if (currRequestExists) {
        PackageRequest request = requests.get(0);
        Log.i(
            TAG,
            "respondToQuery: "
                + request.getParseUser(PackageRequest.KEY_USER).getUsername()
                + ", received "
                + request);
        currRequest = request;
        setMapToCurrRequest(request);
      } else {
        currRequest = null;
        setOnCurrentRequestToFalse();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
      }
    }

    if (requestCode == RECENT_PACKAGE_LIST_REQUEST_CODE) {
      for (PackageRequest request : requests) {
        Log.i(TAG, "respondToQuery: received " + request);
      }
      adapter.clear();
      adapter.addAll(requests);
      this.adapter.notifyDataSetChanged();
    }

  }

  @Override
  public void moveMap(PackageRequest request) {
    setMapToCurrRequest(request);
  }

  private void setMapToCurrRequest(PackageRequest request) {
    Log.i(TAG, "setMapToCurrRequest: here! ");

    onCurrentRequest = true;
    ParseGeoPoint origin = request.getOrigin();
    ParseGeoPoint destination = request.getDestination();

    if (destination == null) {
      return;
    }

    Log.i(TAG, "setMapToCurrRequest: " + destination.getLatitude());

    LatLng latlngOrigin = new LatLng(origin.getLatitude(), origin.getLongitude());
    LatLng latlngDest = new LatLng(destination.getLatitude(), destination.getLongitude());

    setOriginNoCamera(latlngOrigin);
    setDestination(latlngDest);

    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    boundsBuilder.include(latlngDest);
    boundsBuilder.include(latlngOrigin);
    LatLngBounds bounds = boundsBuilder.build();

    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_ZOOM);
    map.animateCamera(cu);
  }

  @Override
  public void onStop() {
    currRequest = null;
    super.onStop();
  }

  @Override
  public void respondToNewSubmission(PackageRequest request) {
    Log.i(TAG, "respondToNewSubmission: new submission from self" + request);
    queryCurrentPackages();
    currRequest = request;
    setMapToCurrRequest(request);

    binding.layoutBottomSheet.chooseDestinationTextView.setText(getActivity().getString(R.string.bottom_search_up_destination));
    binding.layoutBottomSheet.chooseUserTextView.setText(getActivity().getString(R.string.bottom_choose_user_instead));
    bottomSheetComposeBinding.bottomSheetCompose.setVisibility(View.GONE);
    bottomSheetComposeBinding.nextButton.setVisibility(View.GONE);
    bottomSheetComposeBinding.secondDivider.setVisibility(View.GONE);
  }

  @Override
  public void call(final ParseUser user) {
    bottomSheetComposeBinding.chooseUserTextView.setText(String.format("To User: %s", user.getUsername()));

    String placeId =  user.getString(HOME_PLACE);

    binding.pbLoading.setVisibility(View.VISIBLE);
    Snackbar.make(binding.getRoot(), "Loading destination to map", Snackbar.LENGTH_SHORT).show();
    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
    Places.initialize(getContext().getApplicationContext(), BuildConfig.MAPS_API_KEY);
    PlacesClient placesClient = Places.createClient(getContext());

    placesClient.fetchPlace(request).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        binding.pbLoading.setVisibility(View.GONE);

        if (e instanceof ApiException) {
          ApiException apiException = (ApiException) e;
          int statusCode = apiException.getStatusCode();
          // TODO: Handle error with given status code.
          Log.e(TAG, "Place not found: " + e.getMessage());
        }
      }
    }).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
      @Override
      public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
        binding.pbLoading.setVisibility(View.GONE);

        Place place = fetchPlaceResponse.getPlace();
        Log.i(TAG, "Place found: " + place.getName() + fetchPlaceResponse);

        setDestinationPlace(place);
        setDestination(place.getLatLng());
        setBottomVisible();
      }
    });
  }
}
